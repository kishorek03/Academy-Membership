package com.Academy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long S_No;
    private String paymentId;
    private String orderId;
    private String signature;
    private int amount;
    private String currency;
    private LocalDateTime paidAt;
    private String status;
    private int userId;

}
