package com.Academy.controller;

import com.Academy.dto.PaymentRequest;
import com.Academy.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend requests
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    // Create a payment order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest request) {
        try {
            Map<String, Object> order = paymentService.createOrder(request.getAmount(), request.getCurrency());
            return ResponseEntity.ok(order);
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Verify payment (called after user completes payment)
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> paymentData) {
        boolean isValid = paymentService.verifyPayment(paymentData);
        if (isValid) {
            return ResponseEntity.ok("Payment Successful!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed!");
        }
    }
}
