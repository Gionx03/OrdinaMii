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
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @Column(name= "Id", nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name="Username", nullable = false)
    private String username;

    @Column(name = "Email", nullable = false,unique = true)
    private String email;


    @Column(name = "Phone")
    private String phone;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private  LocalDateTime updatedAt;



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username +
                ", email='" + email +
                ", phone='" + phone +
                '}';
    }

}
