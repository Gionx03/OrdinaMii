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
public class Order {

    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
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


    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var or = (Order) o;

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
        return "Order{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", total=" + total +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", user=" + user.getId() +
                '}';
    }

}
