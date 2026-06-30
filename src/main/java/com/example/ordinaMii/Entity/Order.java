package com.example.ordinaMii.Entity;

import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
import com.example.ordinaMii.Entity.Enum.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "OrderDate", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "Total", nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="paymentStatus", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

}
