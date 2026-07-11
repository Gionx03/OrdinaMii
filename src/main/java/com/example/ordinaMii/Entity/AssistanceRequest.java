package com.example.ordinaMii.Entity;

import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AssistanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "Message",nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private AssistanceRequestStatus status;

    @Column(name = "Created_At", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Resolved_At")
    private LocalDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var a = (AssistanceRequest) o;

        if (id == null || a.id == null) {
            return false;
        }

        return id.equals(a.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public String toString() {
        return "AssistanceRequest{" +
                "id=" + id +
                ", message='" + message + "'" +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", resolvedAt=" + resolvedAt +
                ", tableId=" + (table != null ? table.getId() : null) +
                '}';
    }
}
