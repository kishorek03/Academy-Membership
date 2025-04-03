package com.Academy.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private double amount;
    private String currency;
    private String userEmail;  // To track the user making the payment
}
