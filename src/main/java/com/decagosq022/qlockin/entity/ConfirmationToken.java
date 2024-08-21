package com.decagosq022.qlockin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
public class ConfirmationToken extends BaseEntity {

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;


    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User users;


    public ConfirmationToken(User user){
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusDays(1);
        this.users = user;
    }
}
