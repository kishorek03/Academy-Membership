package com.Academy.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private final RazorpayClient razorpayClient;

    public PaymentService() throws RazorpayException {
        this.razorpayClient = new RazorpayClient("YOUR_RAZORPAY_KEY", "YOUR_RAZORPAY_SECRET");
    }

    // Create a new Razorpay order
    public Map<String, Object> createOrder(double amount, String currency) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // Razorpay expects amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.Orders.create(orderRequest);

        // Return order details to frontend
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("id", order.get("id"));
        orderDetails.put("amount", order.get("amount"));
        orderDetails.put("currency", order.get("currency"));
        return orderDetails;
    }

    // Verify Razorpay payment signature
    public boolean verifyPayment(Map<String, String> paymentData) {
        try {
            String razorpayOrderId = paymentData.get("razorpay_order_id");
            String razorpayPaymentId = paymentData.get("razorpay_payment_id");
            String razorpaySignature = paymentData.get("razorpay_signature");

            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec("YOUR_RAZORPAY_SECRET".getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            String generatedSignature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(payload.getBytes()));
            return generatedSignature.equals(razorpaySignature);
        } catch (Exception e) {
            return false;
        }
    }
}
