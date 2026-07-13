package com.example.ordinaMii.Config;

import com.example.ordinaMii.Entity.AssistanceRequest;
import com.example.ordinaMii.Entity.CustomerOrder;
import com.example.ordinaMii.Entity.Dish;
import com.example.ordinaMii.Entity.OrderItem;
import com.example.ordinaMii.Entity.Reservation;
import com.example.ordinaMii.Entity.RestaurantTable;
import com.example.ordinaMii.Entity.User;
import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import com.example.ordinaMii.Entity.Enum.DishCategory;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.OrderType;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.example.ordinaMii.Entity.Enum.Roles;
import com.example.ordinaMii.Repository.AssistanceRequestRepository;
import com.example.ordinaMii.Repository.DishRepository;
import com.example.ordinaMii.Repository.OrderItemRepository;
import com.example.ordinaMii.Repository.OrderRepository;
import com.example.ordinaMii.Repository.ReservationRepository;
import com.example.ordinaMii.Repository.RestaurantTableRepository;
import com.example.ordinaMii.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Profile("demo")
public class DemoDataLoader implements CommandLineRunner {


    private static final UUID ADMIN_DEMO_ID =
            UUID.fromString("a30b4cf9-c29c-4337-8621-2cf9dfadf751");

    private static final UUID CLIENTE_DEMO_ID =
            UUID.fromString("b3b479ec-b391-4f8b-a7f8-c53e34c57ca9");

    private static final UUID CLIENTE2_DEMO_ID =
            UUID.fromString("a2beec99-6bad-4ff9-a7ff-695e1801ee86");

    private static final UUID CLIENTE3_DEMO_ID =
            UUID.fromString("238f3e2a-7dc8-451d-a2b9-14c3858dbd14");

    private static final UUID CLIENTE4_DEMO_ID =
            UUID.fromString("fc400d5d-c87c-45b1-83d8-70b259f237a7");

    private static final UUID CAMERIERE1_DEMO_ID =
            UUID.fromString("1c194394-5807-4547-a714-7fe7c6d0f53b");

    private static final UUID CAMERIERE2_DEMO_ID =
            UUID.fromString("5f11e02e-cf2d-4860-9edf-7b84be9c874b");

    private static final UUID CUOCO1_DEMO_ID =
            UUID.fromString("476483ab-0b00-4438-8673-4d630fc24f94");

    private static final UUID CUOCO2_DEMO_ID =
            UUID.fromString("a3e151b1-0dc9-4406-9cbb-15d854edf8d0");

    private final DishRepository dishRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReservationRepository reservationRepository;
    private final AssistanceRequestRepository assistanceRequestRepository;

    public DemoDataLoader(DishRepository dishRepository,
                          RestaurantTableRepository restaurantTableRepository,
                          UserRepository userRepository,
                          OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          ReservationRepository reservationRepository,
                          AssistanceRequestRepository assistanceRequestRepository) {
        this.dishRepository = dishRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.reservationRepository = reservationRepository;
        this.assistanceRequestRepository = assistanceRequestRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        log.info("Avvio caricamento dati demo OrdinaMii");

        seedDishes();
        seedTables();
        seedUsers();
        seedOrders();
        seedReservations();
        seedAssistanceRequests();

        log.info("Caricamento dati demo OrdinaMii completato");
    }

    private void seedDishes() {

        if (dishRepository.count() > 0) {
            log.info("Piatti già presenti: caricamento piatti demo saltato");
            return;
        }

        saveDish(
                "Bruschetta al pomodoro",
                "Pane tostato con pomodoro fresco, basilico e olio extravergine",
                "4.50",
                DishCategory.ANTIPASTO,
                true,
                "/images/dishes/bruschetta-al-pomodoro.jpg"
        );

        saveDish(
                "Tagliere calabrese",
                "Selezione di salumi, formaggi locali, olive e sottoli",
                "13.00",
                DishCategory.ANTIPASTO,
                true,
                "/images/dishes/tagliere-calabrese.jpg"
        );

        saveDish(
                "Polpette al sugo",
                "Polpette di carne cotte lentamente nel sugo di pomodoro",
                "8.00",
                DishCategory.ANTIPASTO,
                true,
                "/images/dishes/polpette-al-sugo.jpg"
        );

        saveDish(
                "Fiori di zucca fritti",
                "Fiori di zucca ripieni e fritti",
                "7.50",
                DishCategory.ANTIPASTO,
                true,
                "/images/dishes/fiori-di-zucca-fritti.jpg"
        );

        saveDish(
                "Spaghetti alla carbonara",
                "Spaghetti con uova, guanciale, pecorino e pepe",
                "10.00",
                DishCategory.PRIMO,
                true,
                "/images/dishes/spaghetti-alla-carbonara.jpg"
        );

        saveDish(
                "Fileja alla nduja",
                "Pasta fresca calabrese con nduja e pomodoro",
                "11.50",
                DishCategory.PRIMO,
                true,
                "/images/dishes/fileja-alla-nduja.jpg"
        );

        saveDish(
                "Risotto ai funghi porcini",
                "Risotto mantecato con funghi porcini",
                "12.00",
                DishCategory.PRIMO,
                true,
                "/images/dishes/risotto-ai-funghi-porcini.jpg"
        );

        saveDish(
                "Lasagna della casa",
                "Lasagna al ragù con besciamella e parmigiano",
                "10.50",
                DishCategory.PRIMO,
                true,
                "/images/dishes/lasagna-della-casa.jpg"
        );

        saveDish(
                "Tagliata di manzo",
                "Tagliata di manzo con rucola, grana e pomodorini",
                "18.00",
                DishCategory.SECONDO,
                true,
                "/images/dishes/tagliata-di-manzo.jpg"
        );

        saveDish(
                "Cotoletta con patatine",
                "Cotoletta dorata servita con patatine fritte",
                "12.50",
                DishCategory.SECONDO,
                true,
                "/images/dishes/cotoletta-con-patatine.jpg"
        );

        saveDish(
                "Orata al forno",
                "Orata al forno con limone, olive e patate",
                "19.00",
                DishCategory.SECONDO,
                true,
                "/images/dishes/orata-al-forno.jpg"
        );

        saveDish(
                "Hamburger OrdinaMii",
                "Hamburger con cheddar, bacon, insalata e salsa della casa",
                "14.00",
                DishCategory.SECONDO,
                true,
                "/images/dishes/hamburger-ordinamii.jpg"
        );

        saveDish(
                "Patate al forno",
                "Patate al forno con rosmarino",
                "5.00",
                DishCategory.CONTORNO,
                true,
                "/images/dishes/patate-al-forno.jpg"
        );

        saveDish(
                "Verdure grigliate",
                "Zucchine, melanzane e peperoni grigliati",
                "6.00",
                DishCategory.CONTORNO,
                true,
                "/images/dishes/verdure-grigliate.jpg"
        );

        saveDish(
                "Insalata mista",
                "Insalata con lattuga, carote, mais e pomodorini",
                "4.50",
                DishCategory.CONTORNO,
                true,
                "/images/dishes/insalata-mista.jpg"
        );

        saveDish(
                "Patatine fritte",
                "Patatine fritte croccanti",
                "4.50",
                DishCategory.CONTORNO,
                true,
                "/images/dishes/patatine-fritte.jpg"
        );

        saveDish(
                "Tiramisù",
                "Dolce classico con mascarpone, caffè e cacao",
                "6.00",
                DishCategory.DOLCE,
                true,
                "/images/dishes/tiramisu.jpg"
        );

        saveDish(
                "Tartufo di Pizzo",
                "Gelato artigianale al cioccolato e nocciola",
                "6.50",
                DishCategory.DOLCE,
                true,
                "/images/dishes/tartufo-di-pizzo.jpg"
        );

        saveDish(
                "Panna cotta ai frutti di bosco",
                "Panna cotta con salsa ai frutti di bosco",
                "5.50",
                DishCategory.DOLCE,
                true,
                "/images/dishes/panna-cotta-ai-frutti-di-bosco.jpg"
        );

        saveDish(
                "Crostata della casa",
                "Crostata con confettura di albicocche",
                "5.00",
                DishCategory.DOLCE,
                false,
                "/images/dishes/crostata-della-casa.jpg"
        );

        saveDish(
                "Acqua naturale",
                "Bottiglia da 1 litro",
                "2.00",
                DishCategory.BEVANDE,
                true,
                "/images/dishes/acqua-naturale.jpg"
        );

        saveDish(
                "Acqua frizzante",
                "Bottiglia da 1 litro",
                "2.00",
                DishCategory.BEVANDE,
                true,
                "/images/dishes/acqua-frizzante.jpg"
        );

        saveDish(
                "Coca-Cola",
                "Lattina da 33 cl",
                "3.00",
                DishCategory.BEVANDE,
                true,
                "/images/dishes/coca-cola.jpg"
        );

        saveDish(
                "Birra analcolica",
                "Bottiglia da 33 cl",
                "4.00",
                DishCategory.BEVANDE,
                true,
                "/images/dishes/birra-analcolica.jpg"
        );

        saveDish(
                "Caffè",
                "Espresso italiano",
                "1.50",
                DishCategory.BEVANDE,
                true,
                "/images/dishes/caffe.jpg"
        );
    }

    private void saveDish(String name,
                          String description,
                          String price,
                          DishCategory category,
                          boolean available,
                          String imageUrl) {

        Dish dish = new Dish();
        dish.setName(name);
        dish.setDescription(description);
        dish.setPrice(new BigDecimal(price));
        dish.setCategory(category);
        dish.setAvailable(available);
        dish.setImageUrl(imageUrl);

        dishRepository.save(dish);
    }

    private void seedTables() {

        if (restaurantTableRepository.count() > 0) {
            log.info("Tavoli già presenti: caricamento tavoli demo saltato");
            return;
        }

        saveTable(1, 2, true);
        saveTable(2, 2, true);
        saveTable(3, 4, true);
        saveTable(4, 4, true);
        saveTable(5, 4, true);
        saveTable(6, 6, true);
        saveTable(7, 6, true);
        saveTable(8, 8, true);
        saveTable(9, 10, true);
        saveTable(10, 12, true);

        saveTable(98, 4, false);
        saveTable(99, 2, false);
    }

    private void saveTable(int number, int seats, boolean active) {

        RestaurantTable table = new RestaurantTable();
        table.setNumber(number);
        table.setSeats(seats);
        table.setActive(active);

        restaurantTableRepository.save(table);
    }

    private void seedUsers() {

        saveUserIfAbsent(
                ADMIN_DEMO_ID,
                "admin.demo",
                "admin.demo@ordinamii.com",
                Roles.ADMIN,
                "3330000001"
        );

        saveUserIfAbsent(
                CLIENTE_DEMO_ID,
                "cliente.demo",
                "cliente.demo@ordinamii.it",
                Roles.CLIENTE,
                "3331000001"
        );

        saveUserIfAbsent(
                CLIENTE2_DEMO_ID,
                "cliente2.demo",
                "cliente2.demo@ordinamii.it",
                Roles.CLIENTE,
                "3331000002"
        );

        saveUserIfAbsent(
                CLIENTE3_DEMO_ID,
                "cliente3.demo",
                "cliente3.demo@ordinamii.it",
                Roles.CLIENTE,
                "3331000003"
        );

        saveUserIfAbsent(
                CLIENTE4_DEMO_ID,
                "cliente4.demo",
                "cliente4.demo@ordinamii.it",
                Roles.CLIENTE,
                "3331000004"
        );

        saveUserIfAbsent(
                CAMERIERE1_DEMO_ID,
                "cameriere1.demo",
                "cameriere1.demo@ordinamii.com",
                Roles.CAMERIERE,
                "3332000001"
        );

        saveUserIfAbsent(
                CAMERIERE2_DEMO_ID,
                "cameriere2.demo",
                "cameriere2.demo@ordinamii.com",
                Roles.CAMERIERE,
                "3332000002"
        );

        saveUserIfAbsent(
                CUOCO1_DEMO_ID,
                "cuoco1.demo",
                "cuoco1.demo@ordinamii.com",
                Roles.CUOCO,
                "3333000001"
        );

        saveUserIfAbsent(
                CUOCO2_DEMO_ID,
                "cuoco2.demo",
                "cuoco2.demo@ordinamii.com",
                Roles.CUOCO,
                "3333000002"
        );
    }

    private void saveUserIfAbsent(UUID id,
                                  String username,
                                  String email,
                                  Roles role,
                                  String phone) {

        if (userRepository.existsById(id)) {
            return;
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Utente demo con email {} già presente con id diverso. Controllare gli ID Keycloak.", email);
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPhone(phone);
        user.setCreatedAt(now.minusDays(30));
        user.setUpdatedAt(now);

        userRepository.save(user);
    }

    private void seedOrders() {

        if (orderRepository.count() > 0) {
            log.info("Ordini già presenti: caricamento ordini demo saltato");
            return;
        }

        User cliente1 = findUser(CLIENTE_DEMO_ID);
        User cliente2 = findUser(CLIENTE2_DEMO_ID);
        User cliente3 = findUser(CLIENTE3_DEMO_ID);
        User cliente4 = findUser(CLIENTE4_DEMO_ID);

        RestaurantTable table1 = findTableByNumber(1);
        RestaurantTable table2 = findTableByNumber(2);
        RestaurantTable table3 = findTableByNumber(3);
        RestaurantTable table4 = findTableByNumber(4);
        RestaurantTable table5 = findTableByNumber(5);
        RestaurantTable table6 = findTableByNumber(6);
        RestaurantTable table8 = findTableByNumber(8);

        CustomerOrder order1 = createOrder(
                cliente1,
                null,
                OrderType.TAKE_AWAY,
                OrderStatus.PENDING,
                PaymentStatus.NOT_PAID,
                LocalDateTime.now().minusMinutes(15)
        );
        addItem(order1, "Spaghetti alla carbonara", 2);
        addItem(order1, "Acqua naturale", 1);
        updateTotal(order1);

        CustomerOrder order2 = createOrder(
                cliente1,
                table3,
                OrderType.ON_THE_TABLE,
                OrderStatus.PREPARING,
                PaymentStatus.PAY_AT_COUNTER,
                LocalDateTime.now().minusMinutes(40)
        );
        addItem(order2, "Bruschetta al pomodoro", 2);
        addItem(order2, "Tagliata di manzo", 2);
        addItem(order2, "Patate al forno", 2);
        addItem(order2, "Coca-Cola", 2);
        updateTotal(order2);

        CustomerOrder order3 = createOrder(
                cliente2,
                table4,
                OrderType.ON_THE_TABLE,
                OrderStatus.SERVED,
                PaymentStatus.PAID,
                LocalDateTime.now().minusHours(2)
        );
        addItem(order3, "Tagliere calabrese", 1);
        addItem(order3, "Fileja alla nduja", 2);
        addItem(order3, "Tartufo di Pizzo", 2);
        addItem(order3, "Acqua frizzante", 1);
        updateTotal(order3);

        CustomerOrder order4 = createOrder(
                cliente2,
                null,
                OrderType.TAKE_AWAY,
                OrderStatus.SERVED,
                PaymentStatus.PAID,
                LocalDateTime.now().minusDays(1).plusHours(3)
        );
        addItem(order4, "Lasagna della casa", 2);
        addItem(order4, "Cotoletta con patatine", 1);
        addItem(order4, "Tiramisù", 2);
        updateTotal(order4);

        CustomerOrder order5 = createOrder(
                cliente3,
                table5,
                OrderType.ON_THE_TABLE,
                OrderStatus.PENDING,
                PaymentStatus.NOT_PAID,
                LocalDateTime.now().minusMinutes(5)
        );
        addItem(order5, "Polpette al sugo", 1);
        addItem(order5, "Risotto ai funghi porcini", 2);
        addItem(order5, "Verdure grigliate", 1);
        updateTotal(order5);

        CustomerOrder order6 = createOrder(
                cliente3,
                table6,
                OrderType.ON_THE_TABLE,
                OrderStatus.CANCELLED,
                PaymentStatus.CANCELLED,
                LocalDateTime.now().minusDays(2).plusHours(4)
        );
        addItem(order6, "Hamburger OrdinaMii", 2);
        addItem(order6, "Patatine fritte", 2);
        addItem(order6, "Coca-Cola", 2);
        updateTotal(order6);

        CustomerOrder order7 = createOrder(
                cliente4,
                null,
                OrderType.TAKE_AWAY,
                OrderStatus.PREPARING,
                PaymentStatus.PENDING,
                LocalDateTime.now().minusMinutes(25)
        );
        addItem(order7, "Orata al forno", 1);
        addItem(order7, "Insalata mista", 1);
        addItem(order7, "Acqua naturale", 1);
        updateTotal(order7);

        CustomerOrder order8 = createOrder(
                cliente4,
                table8,
                OrderType.ON_THE_TABLE,
                OrderStatus.SERVED,
                PaymentStatus.PAID,
                LocalDateTime.now().minusDays(3).plusHours(2)
        );
        addItem(order8, "Fiori di zucca fritti", 3);
        addItem(order8, "Spaghetti alla carbonara", 3);
        addItem(order8, "Tagliata di manzo", 2);
        addItem(order8, "Panna cotta ai frutti di bosco", 3);
        addItem(order8, "Caffè", 3);
        updateTotal(order8);

        CustomerOrder order9 = createOrder(
                cliente1,
                table1,
                OrderType.ON_THE_TABLE,
                OrderStatus.SERVED,
                PaymentStatus.PAID,
                LocalDateTime.now().minusDays(7).plusHours(1)
        );
        addItem(order9, "Bruschetta al pomodoro", 1);
        addItem(order9, "Fileja alla nduja", 1);
        addItem(order9, "Tiramisù", 1);
        updateTotal(order9);

        CustomerOrder order10 = createOrder(
                cliente2,
                table2,
                OrderType.ON_THE_TABLE,
                OrderStatus.PREPARING,
                PaymentStatus.PAY_AT_COUNTER,
                LocalDateTime.now().minusMinutes(55)
        );
        addItem(order10, "Polpette al sugo", 2);
        addItem(order10, "Hamburger OrdinaMii", 2);
        addItem(order10, "Birra analcolica", 2);
        updateTotal(order10);
    }

    private CustomerOrder createOrder(User user,
                                      RestaurantTable table,
                                      OrderType orderType,
                                      OrderStatus status,
                                      PaymentStatus paymentStatus,
                                      LocalDateTime orderDate) {

        if (user == null) {
            log.warn("Ordine demo saltato: utente non presente");
            return null;
        }

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setTable(table);
        order.setOrderType(orderType);
        order.setStatus(status);
        order.setPaymentStatus(paymentStatus);
        order.setOrderDate(orderDate);
        order.setTotal(BigDecimal.ZERO);

        return orderRepository.save(order);
    }

    private void addItem(CustomerOrder order, String dishName, int quantity) {

        if (order == null) {
            return;
        }

        Dish dish = findDishByName(dishName);

        if (dish == null) {
            log.warn("Piatto demo non trovato: {}", dishName);
            return;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setCustomerOrder(order);
        orderItem.setDish(dish);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(dish.getPrice());

        orderItemRepository.save(orderItem);
    }

    private void updateTotal(CustomerOrder order) {

        if (order == null) {
            return;
        }

        List<OrderItem> orderItems = orderItemRepository.findByCustomerOrder_Id(order.getId());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            BigDecimal lineTotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            total = total.add(lineTotal);
        }

        order.setTotal(total);
        orderRepository.save(order);
    }

    private void seedReservations() {

        if (reservationRepository.count() > 0) {
            log.info("Prenotazioni già presenti: caricamento prenotazioni demo saltato");
            return;
        }

        User cliente1 = findUser(CLIENTE_DEMO_ID);
        User cliente2 = findUser(CLIENTE2_DEMO_ID);
        User cliente3 = findUser(CLIENTE3_DEMO_ID);
        User cliente4 = findUser(CLIENTE4_DEMO_ID);

        saveReservation(cliente1, 1, LocalDate.now().plusDays(1), LocalTime.of(20, 30), 2, ReservationStatus.CONFIRMED);
        saveReservation(cliente2, 3, LocalDate.now().plusDays(1), LocalTime.of(21, 0), 4, ReservationStatus.CONFIRMED);
        saveReservation(cliente3, 6, LocalDate.now().plusDays(2), LocalTime.of(19, 30), 5, ReservationStatus.CONFIRMED);
        saveReservation(cliente4, 8, LocalDate.now().plusDays(3), LocalTime.of(20, 0), 8, ReservationStatus.CONFIRMED);

        saveReservation(cliente1, 2, LocalDate.now().minusDays(1), LocalTime.of(20, 30), 2, ReservationStatus.COMPLETED);
        saveReservation(cliente2, 4, LocalDate.now().minusDays(2), LocalTime.of(21, 0), 4, ReservationStatus.COMPLETED);
        saveReservation(cliente3, 5, LocalDate.now().minusDays(5), LocalTime.of(19, 45), 3, ReservationStatus.COMPLETED);

        saveReservation(cliente4, 7, LocalDate.now().plusDays(4), LocalTime.of(20, 30), 6, ReservationStatus.CANCELLED);
        saveReservation(cliente3, 7, LocalDate.now().plusDays(4), LocalTime.of(20, 30), 4, ReservationStatus.CONFIRMED);

        saveReservation(cliente2, 9, LocalDate.now().plusDays(6), LocalTime.of(22, 0), 9, ReservationStatus.CONFIRMED);
    }

    private void saveReservation(User user,
                                 int tableNumber,
                                 LocalDate date,
                                 LocalTime time,
                                 int numberOfPeople,
                                 ReservationStatus status) {

        if (user == null) {
            log.warn("Prenotazione demo saltata: utente non presente");
            return;
        }

        RestaurantTable table = findTableByNumber(tableNumber);

        if (table == null) {
            log.warn("Prenotazione demo saltata: tavolo {} non trovato", tableNumber);
            return;
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setStatus(status);

        reservationRepository.save(reservation);
    }

    private void seedAssistanceRequests() {

        if (assistanceRequestRepository.count() > 0) {
            log.info("Richieste assistenza già presenti: caricamento richieste demo saltato");
            return;
        }

        saveAssistanceRequest(1, "Serve acqua naturale al tavolo", AssistanceRequestStatus.PENDING, LocalDateTime.now().minusMinutes(8), null);
        saveAssistanceRequest(3, "Il cliente chiede informazioni sugli allergeni", AssistanceRequestStatus.PENDING, LocalDateTime.now().minusMinutes(15), null);
        saveAssistanceRequest(4, "Richiesto pagamento al tavolo", AssistanceRequestStatus.RESOLVED, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        saveAssistanceRequest(6, "Richiesta sostituzione posate", AssistanceRequestStatus.RESOLVED, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        saveAssistanceRequest(8, "Il cliente ha annullato la richiesta", AssistanceRequestStatus.CANCELLED, LocalDateTime.now().minusHours(3), null);
        saveAssistanceRequest(5, "Serve pane al tavolo", AssistanceRequestStatus.PENDING, LocalDateTime.now().minusMinutes(3), null);
    }

    private void saveAssistanceRequest(int tableNumber,
                                       String message,
                                       AssistanceRequestStatus status,
                                       LocalDateTime createdAt,
                                       LocalDateTime resolvedAt) {

        RestaurantTable table = findTableByNumber(tableNumber);

        if (table == null) {
            log.warn("Richiesta assistenza demo saltata: tavolo {} non trovato", tableNumber);
            return;
        }

        AssistanceRequest request = new AssistanceRequest();
        request.setTable(table);
        request.setMessage(message);
        request.setStatus(status);
        request.setCreatedAt(createdAt);
        request.setResolvedAt(resolvedAt);

        assistanceRequestRepository.save(request);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    private RestaurantTable findTableByNumber(int number) {

        List<RestaurantTable> tables = restaurantTableRepository.findAll();

        for (RestaurantTable table : tables) {
            if (table.getNumber() == number) {
                return table;
            }
        }

        return null;
    }

    private Dish findDishByName(String name) {

        List<Dish> dishes = dishRepository.findAll();

        for (Dish dish : dishes) {
            if (dish.getName().equals(name)) {
                return dish;
            }
        }

        return null;
    }
}
