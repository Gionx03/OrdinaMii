package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.*;
import com.example.ordinaMii.DTO.Response.AssistanceRequestResponseDTO;
import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.Entity.*;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.OrderType;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
import com.example.ordinaMii.Exceptions.BadRequestException;
import com.example.ordinaMii.Exceptions.ConflictException;
import com.example.ordinaMii.Exceptions.ResourceNotFoundException;
import com.example.ordinaMii.Mapper.OrderItemMapper;
import com.example.ordinaMii.Mapper.OrderMapper;
import com.example.ordinaMii.Repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final RestaurantTableRepository restaurantTableRepository;
    private final AssistanceRequestService assistanceRequestService;
    private final FakePaymentService fakePaymentService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        DishRepository dishRepository,
                        OrderMapper orderMapper,
                        OrderItemMapper orderItemMapper,
                        RestaurantTableRepository restaurantTableRepository,
                        AssistanceRequestService assistanceRequestService,
                        FakePaymentService fakePaymentService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.restaurantTableRepository = restaurantTableRepository;
        this.assistanceRequestService = assistanceRequestService;
        this.fakePaymentService = fakePaymentService;
    }


    @Transactional(readOnly = true)
    @Cacheable(
            value = "orderSearch",
            key = "#status + '_' + #customerId + '_' + #data + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<OrderResponseDTO> getOrders(
            OrderStatus status,
            UUID customerId,
            LocalDate data,
            Pageable pageable) {

        log.info("Recupero lista ordini. status={}, customerId={}, data={}",
                status, customerId, data);

        LocalDateTime startDate;
        LocalDateTime endDate;

        if (data == null) {
            startDate = LocalDateTime.of(1900, 1, 1, 0, 0);
            endDate = LocalDateTime.of(3000, 1, 1, 0, 0);
        } else {
            startDate = data.atStartOfDay();
            endDate = data.plusDays(1).atStartOfDay();
        }

        Page<CustomerOrder> orders = orderRepository.searchOrders(
                status,
                customerId,
                startDate,
                endDate,
                pageable
        );

        return orders.map(orderMapper::toResponseDTO);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "orderById", key = "#id")
    public OrderResponseDTO getOrderById(UUID id) {

        log.info("Recupero ordine con id={}", id);

        CustomerOrder customerOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con id: " + id));

        List<OrderItem> orderItems = orderItemRepository.findByCustomerOrder_Id(id);

        return orderMapper.toResponseDTO(customerOrder, orderItems);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#result.id()")
            }
    )
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {

        log.info("Creazione nuovo ordine per userId={}", orderRequestDTO.userId());

        return createOrderInternal(
                orderRequestDTO.userId(),
                orderRequestDTO.orderType(),
                orderRequestDTO.tableId(),
                orderRequestDTO.items()
        );
    }



    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#result.id()")
            }
    )
    public OrderResponseDTO createMyOrder(UUID userId, MyOrderRequestDTO myOrderRequestDTO) {

        log.info("Creazione nuovo ordine personale per userId={}", userId);

        return createOrderInternal(
                userId,
                myOrderRequestDTO.orderType(),
                myOrderRequestDTO.tableId(),
                myOrderRequestDTO.items()
        );
    }

    private OrderResponseDTO createOrderInternal(
            UUID userId,
            OrderType orderType,
            UUID tableId,
            List<OrderItemRequestDTO> items) {

        validateOrderItems(items);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente non trovato con id: " + userId
                ));

        RestaurantTable table = resolveTableForOrder(orderType, tableId);

        BigDecimal total = calculateTotal(items);

        CustomerOrder customerOrder = orderMapper.toEntity(
                user,
                table,
                total,
                OrderStatus.PENDING,
                PaymentStatus.NOT_PAID,
                orderType
        );

        CustomerOrder savedCustomerOrder = orderRepository.save(customerOrder);

        List<OrderItem> orderItems = createOrderItems(items, savedCustomerOrder);

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        log.info("Ordine creato con id={}", savedCustomerOrder.getId());

        return orderMapper.toResponseDTO(savedCustomerOrder, savedOrderItems);
    }


    private RestaurantTable resolveTableForOrder(OrderType orderType, UUID tableId) {

        if (orderType == null) {
            throw new BadRequestException("Il tipo di ordine è obbligatorio");
        }

        if (orderType == OrderType.TAKE_AWAY) {

            if (tableId != null) {
                throw new BadRequestException("Un ordine d'asporto non può essere associato a un tavolo");
            }

            return null;
        }

        if (orderType == OrderType.ON_THE_TABLE) {

            if (tableId == null) {
                throw new BadRequestException("Un ordine al tavolo deve essere associato a un tavolo");
            }

            RestaurantTable table = restaurantTableRepository.findById(tableId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tavolo non trovato con id: " + tableId));

            if (!table.isActive()) {
                throw new BadRequestException(
                        "Non è possibile associare l'ordine a un tavolo disattivato"
                );
            }

            return table;
        }

        throw new BadRequestException("Tipo di ordine non valido");
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#id")
            }
    )
    public OrderResponseDTO updateOrder(UUID id, OrderRequestDTO orderRequestDTO) {

        log.info("Aggiornamento ordine con id={}", id);

        validateOrderItems(orderRequestDTO.items());

        CustomerOrder customerOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con id: " + id));

        validateOrderCanBeModified(customerOrder);

        User user = userRepository.findById(orderRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente non trovato con id: " + orderRequestDTO.userId()
                ));

        RestaurantTable table = resolveTableForOrder(
                orderRequestDTO.orderType(),
                orderRequestDTO.tableId()
        );

        BigDecimal total = calculateTotal(orderRequestDTO.items());

        customerOrder.setUser(user);
        customerOrder.setTable(table);
        customerOrder.setOrderType(orderRequestDTO.orderType());
        customerOrder.setTotal(total);

        orderItemRepository.deleteByCustomerOrder_Id(id);

        List<OrderItem> newOrderItems = createOrderItems(orderRequestDTO.items(), customerOrder);

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(newOrderItems);

        CustomerOrder updatedCustomerOrder = orderRepository.save(customerOrder);

        log.info("Ordine aggiornato con id={}", updatedCustomerOrder.getId());

        return orderMapper.toResponseDTO(updatedCustomerOrder, savedOrderItems);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#id")
            }
    )
    public OrderResponseDTO updateOrderStatus(
            UUID id,
            OrderStatusUpdateRequestDTO orderStatusUpdateRequestDTO) {

        log.info("Aggiornamento stato ordine con id={}, nuovo stato={}",
                id, orderStatusUpdateRequestDTO.status());

        CustomerOrder customerOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordine non trovato con id: " + id
                ));

        OrderStatus newStatus = orderStatusUpdateRequestDTO.status();

        validateOrderStatusTransition(customerOrder, newStatus);

        applyOrderStatusTransition(customerOrder, newStatus);

        CustomerOrder updatedCustomerOrder = orderRepository.save(customerOrder);

        List<OrderItem> orderItems = orderItemRepository.findByCustomerOrder_Id(id);

        log.info("Stato ordine aggiornato con id={}", updatedCustomerOrder.getId());

        return orderMapper.toResponseDTO(updatedCustomerOrder, orderItems);
    }


    private void applyOrderStatusTransition(
            CustomerOrder customerOrder,
            OrderStatus newStatus) {

        customerOrder.setStatus(newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            customerOrder.setPaymentStatus(PaymentStatus.CANCELLED);
        }
    }



    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#id")
            }
    )
    public OrderResponseDTO deleteOrder(UUID id) {

        log.info("Cancellazione logica ordine con id={}", id);

        CustomerOrder customerOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordine non trovato con id: " + id
                ));

        validateOrderStatusTransition(customerOrder, OrderStatus.CANCELLED);

        applyOrderStatusTransition(customerOrder, OrderStatus.CANCELLED);

        CustomerOrder cancelledOrder = orderRepository.save(customerOrder);

        List<OrderItem> orderItems = orderItemRepository.findByCustomerOrder_Id(id);

        log.info("Ordine cancellato logicamente con id={}", cancelledOrder.getId());

        return orderMapper.toResponseDTO(cancelledOrder, orderItems);
    }


    @Transactional(readOnly = true)
    @Cacheable(
            value = "myOrderSearch",
            key = "'user_' + #userId + '_' + #status + '_' + #startDate + '_' " +
                    "+ #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<OrderResponseDTO> getMyOrders(
            UUID userId,
            OrderStatus status,
            LocalDate startDate,
            Pageable pageable) {

        log.info("Recupero ordini personali per userId={}, status={}, startDate={}",
                userId, status, startDate);

        return getOrdersByUser(userId, status, startDate, pageable);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#result.id()")
            }
    )
    public OrderResponseDTO payMyOrder(UUID userId, UUID orderId) {

        CustomerOrder customerOrder = getOwnedOrder(userId, orderId);

        if (customerOrder.getPaymentStatus() == PaymentStatus.PENDING) {
            throw new ConflictException(
                    "Il pagamento tramite cameriere è già stato richiesto"
            );
        }

        validatePaymentStatusTransition(customerOrder, PaymentStatus.PAID);

        FakePaymentService.FakePaymentReceipt receipt =
                fakePaymentService.pay(customerOrder.getId(), customerOrder.getTotal());

        customerOrder.setPaymentStatus(PaymentStatus.PAID);

        CustomerOrder paidOrder = orderRepository.save(customerOrder);
        List<OrderItem> orderItems = orderItemRepository.findByCustomerOrder_Id(orderId);

        log.info(
                "Ordine personale pagato. orderId={}, userId={}, transactionId={}",
                orderId,
                userId,
                receipt.transactionId()
        );

        return orderMapper.toResponseDTO(paidOrder, orderItems);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#result.id()")
            }
    )
    public OrderResponseDTO requestWaiterPayment(UUID userId, UUID orderId) {

        CustomerOrder customerOrder = getOwnedOrder(userId, orderId);
        RestaurantTable table = requireOrderTable(customerOrder);

        validatePaymentStatusTransition(customerOrder, PaymentStatus.PENDING);

        customerOrder.setPaymentStatus(PaymentStatus.PENDING);
        CustomerOrder updatedOrder = orderRepository.save(customerOrder);

        assistanceRequestService.createAssistanceRequest(
                new AssistanceRequestDTO(
                        "Pagamento al tavolo richiesto per l'ordine #" + orderId,
                        table.getId()
                )
        );

        List<OrderItem> orderItems = orderItemRepository.findByCustomerOrder_Id(orderId);

        log.info(
                "Pagamento tramite cameriere richiesto. orderId={}, userId={}, tableId={}",
                orderId,
                userId,
                table.getId()
        );

        return orderMapper.toResponseDTO(updatedOrder, orderItems);
    }

    @Transactional
    public AssistanceRequestResponseDTO requestAssistanceForMyOrder(
            UUID userId,
            UUID orderId) {

        CustomerOrder customerOrder = getOwnedOrder(userId, orderId);

        if (customerOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile richiedere assistenza per un ordine cancellato"
            );
        }

        RestaurantTable table = requireOrderTable(customerOrder);

        return assistanceRequestService.createAssistanceRequest(
                new AssistanceRequestDTO(
                        "Richiesta assistenza per l'ordine #" + orderId,
                        table.getId()
                )
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "userOrderSearch",
            key = "#userId + '_' + #status + '_' + #startDate + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<OrderResponseDTO> getOrdersByUser(
            UUID userId,
            OrderStatus status,
            LocalDate startDate,
            Pageable pageable) {

        log.info("Recupero ordini per userId={}, status={}, startDate={}",
                userId, status, startDate);

        LocalDateTime normalizedStartDate;

        if (startDate == null) {
            normalizedStartDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        } else {
            normalizedStartDate = startDate.atStartOfDay();
        }

        Page<CustomerOrder> orders = orderRepository.searchOrdersByUserFromDate(
                userId,
                status,
                normalizedStartDate,
                pageable
        );

        return orders.map(orderMapper::toResponseDTO);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "orderSearch", allEntries = true),
                    @CacheEvict(value = "myOrderSearch", allEntries = true),
                    @CacheEvict(value = "userOrderSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "orderById", key = "#id")
            }
    )
    public OrderResponseDTO updatePaymentStatus(
            UUID id,
            PaymentStatusUpdateRequestDTO paymentStatusUpdateRequestDTO) {

        log.info("Aggiornamento stato pagamento ordine con id={}, nuovo paymentStatus={}",
                id, paymentStatusUpdateRequestDTO.paymentStatus());

        CustomerOrder customerOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con id: " + id));

        validatePaymentStatusTransition(
                customerOrder,
                paymentStatusUpdateRequestDTO.paymentStatus()
        );

        customerOrder.setPaymentStatus(paymentStatusUpdateRequestDTO.paymentStatus());

        CustomerOrder updatedCustomerOrder = orderRepository.save(customerOrder);

        List<OrderItem> orderItems = orderItemRepository.findByCustomerOrder_Id(id);

        log.info("Stato pagamento ordine aggiornato con id={}", updatedCustomerOrder.getId());

        return orderMapper.toResponseDTO(updatedCustomerOrder, orderItems);
    }

    private void validateOrderCanBeModified(CustomerOrder customerOrder) {

        if (customerOrder.getStatus() == OrderStatus.SERVED) {
            throw new ConflictException(
                    "Non è possibile modificare un ordine già servito"
            );
        }

        if (customerOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare un ordine cancellato"
            );
        }

        if (customerOrder.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ConflictException(
                    "Non è possibile modificare un ordine con pagamento già effettuato"
            );
        }

        if (customerOrder.getPaymentStatus() == PaymentStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare un ordine con pagamento annullato"
            );
        }
    }

    private CustomerOrder getOwnedOrder(UUID userId, UUID orderId) {

        return orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordine personale non trovato con id: " + orderId
                ));
    }

    private RestaurantTable requireOrderTable(CustomerOrder customerOrder) {

        if (customerOrder.getTable() == null) {
            throw new BadRequestException(
                    "Questa operazione è disponibile solo per un ordine al tavolo"
            );
        }

        return customerOrder.getTable();
    }

    private void validateOrderStatusTransition(
            CustomerOrder customerOrder,
            OrderStatus newStatus) {

        if (newStatus == null) {
            throw new BadRequestException("Lo stato dell'ordine è obbligatorio");
        }

        OrderStatus currentStatus = customerOrder.getStatus();

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare lo stato di un ordine cancellato"
            );
        }

        if (currentStatus == OrderStatus.SERVED) {
            throw new ConflictException(
                    "Non è possibile modificare lo stato di un ordine già servito"
            );
        }

        if (currentStatus == newStatus) {
            throw new ConflictException(
                    "L'ordine si trova già nello stato " + newStatus
            );
        }

        if (currentStatus == OrderStatus.PENDING
                && newStatus != OrderStatus.PREPARING
                && newStatus != OrderStatus.CANCELLED) {
            throw new ConflictException(
                    "Un ordine PENDING può passare solo a PREPARING o CANCELLED"
            );
        }

        if (currentStatus == OrderStatus.PREPARING
                && newStatus != OrderStatus.SERVED
                && newStatus != OrderStatus.CANCELLED) {
            throw new ConflictException(
                    "Un ordine PREPARING può passare solo a SERVED o CANCELLED"
            );
        }

        if (newStatus == OrderStatus.CANCELLED
                && customerOrder.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ConflictException(
                    "Non è possibile cancellare un ordine già pagato"
            );
        }
    }

    private void validateOrderItems(List<OrderItemRequestDTO> items) {

        if (items == null || items.isEmpty()) {
            throw new BadRequestException("L'ordine deve contenere almeno un piatto");
        }

        for (OrderItemRequestDTO item : items) {
            if (item.quantity() <= 0) {
                throw new BadRequestException("La quantità deve essere maggiore di zero");
            }
        }
    }

    private BigDecimal calculateTotal(List<OrderItemRequestDTO> items) {

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO item : items) {

            Dish dish = dishRepository.findById(item.dishId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Piatto non trovato con id: " + item.dishId()
                    ));

            if (!dish.isAvailable()) {
                throw new BadRequestException("Il piatto non è disponibile: " + dish.getName());
            }

            BigDecimal unitPrice = dish.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(item.quantity());

            total = total.add(unitPrice.multiply(quantity));
        }

        return total;
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequestDTO> items, CustomerOrder customerOrder) {

        return items.stream()
                .map(item -> {
                    Dish dish = dishRepository.findById(item.dishId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Piatto non trovato con id: " + item.dishId()
                            ));

                    if (!dish.isAvailable()) {
                        throw new BadRequestException("Il piatto non è disponibile: " + dish.getName());
                    }

                    return orderItemMapper.toEntity(item, dish, customerOrder);
                })
                .toList();
    }

    private LocalDateTime getStartDateTime(LocalDate data) {

        if (data == null) {
            return null;
        }

        return data.atStartOfDay();
    }

    private LocalDateTime getEndDateTime(LocalDate data) {

        if (data == null) {
            return null;
        }

        return data.plusDays(1).atStartOfDay();
    }

    private void validatePaymentStatusTransition(
            CustomerOrder customerOrder,
            PaymentStatus newPaymentStatus) {

        if (newPaymentStatus == null) {
            throw new BadRequestException("Lo stato del pagamento è obbligatorio");
        }

        PaymentStatus currentPaymentStatus = customerOrder.getPaymentStatus();

        if (currentPaymentStatus == PaymentStatus.PAID) {
            throw new ConflictException(
                    "Non è possibile modificare un pagamento già effettuato"
            );
        }

        if (currentPaymentStatus == PaymentStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare un pagamento annullato"
            );
        }

        if (currentPaymentStatus == newPaymentStatus) {
            throw new ConflictException(
                    "Il pagamento si trova già nello stato " + newPaymentStatus
            );
        }

        if (customerOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare il pagamento di un ordine cancellato"
            );
        }

        if (newPaymentStatus == PaymentStatus.CANCELLED) {
            throw new ConflictException(
                    "Il pagamento viene annullato solo quando viene cancellato l'ordine"
            );
        }

        if (currentPaymentStatus == PaymentStatus.NOT_PAID
                && newPaymentStatus != PaymentStatus.PENDING
                && newPaymentStatus != PaymentStatus.PAY_AT_COUNTER
                && newPaymentStatus != PaymentStatus.PAID) {
            throw new ConflictException(
                    "Un pagamento NOT_PAID può passare solo a PENDING, PAY_AT_COUNTER o PAID"
            );
        }

        if (currentPaymentStatus == PaymentStatus.PENDING
                && newPaymentStatus != PaymentStatus.PAID) {
            throw new ConflictException(
                    "Un pagamento PENDING può passare solo a PAID"
            );
        }

        if (currentPaymentStatus == PaymentStatus.PAY_AT_COUNTER
                && newPaymentStatus != PaymentStatus.PAID) {
            throw new ConflictException(
                    "Un pagamento PAY_AT_COUNTER può passare solo a PAID"
            );
        }
    }
}
