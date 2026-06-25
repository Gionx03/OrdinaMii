package com.example.ordinaMii.Entity;

import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "Message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private AssistanceRequestStatus status;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ResolvedAt", nullable = false)
    private LocalDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        AssistanceRequest other = (AssistanceRequest) obj;

        return id != null && id.equals(other.id);
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
