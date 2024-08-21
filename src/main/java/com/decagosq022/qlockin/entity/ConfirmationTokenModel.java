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
public class ConfirmationTokenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User users;


    public ConfirmationTokenModel (User user){
        this.token = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.expiryDate = creationDate.plusDays(1);
        this.users = user;
    }
}
