package com.Academy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
public class PaymentEntity {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long S_No;
    @Setter
    private String paymentId;
    @Setter
    private String orderId;
    @Setter
    private String signature;
    @Setter
    private int amount;
    @Setter
    private String currency;
    @Setter
    private LocalDateTime paidAt;
    @Setter
    private String status;

    @Setter
    private int userId;

}
