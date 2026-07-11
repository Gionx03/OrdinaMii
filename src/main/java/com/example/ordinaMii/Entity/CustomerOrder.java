package com.example.ordinaMii.Entity;

import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.OrderType;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
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
public class CustomerOrder {

    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "Order_Date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "Total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="payment_status", nullable = false)
    private PaymentStatus paymentStatus;


    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable table;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var or = (CustomerOrder) o;

        if (id == null || or.id == null) {
            return false;
        }

        return id.equals(or.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public String toString() {
        return "CustomerOrder{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", total=" + total +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", orderType=" + orderType +
                ", table=" + (table != null ? table.getId() : null) +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }

}
