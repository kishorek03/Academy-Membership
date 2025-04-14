package com.Academy.controller;

import com.Academy.model.PaymentEntity;
import com.Academy.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;
    @PostMapping("/update")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> payload) {
        logger.info("Received payment payload: {}", payload);
        try {
            PaymentEntity payment = new PaymentEntity();
            payment.setPaymentId((String) payload.get("razorpay_payment_id"));
            payment.setOrderId((String) payload.get("razorpay_order_id"));
            payment.setSignature((String) payload.get("razorpay_signature"));
            payment.setAmount((int) payload.get("amount"));
            payment.setCurrency((String) payload.get("currency"));
            payment.setPaidAt(LocalDateTime.now());
            payment.setStatus("PAID");
            payment.setUserId((int) payload.getOrDefault("userid", 0));
            logger.info("Mapped PaymentEntity: {}", payment);
            paymentService.savePayment(payment);
            logger.info("Payment saved successfully for paymentId: {}", payment.getPaymentId());
            return ResponseEntity.ok("Payment verified and saved successfully");
        } catch (Exception e) {
            logger.error("Error saving payment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save payment");
        }
    }
}
