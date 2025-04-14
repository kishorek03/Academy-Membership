package com.Academy.service;

import com.Academy.model.PaymentEntity;
import com.Academy.repo.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;

    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private PaymentRepository paymentRepository;

    public void savePayment(PaymentEntity payment) {
        paymentRepository.save(payment);
    }


    public PaymentService() throws RazorpayException {
        this.razorpayClient = new RazorpayClient("YOUR_RAZORPAY_KEY", "YOUR_RAZORPAY_SECRET");
    }

    public Map<String, Object> createOrder(double amount, String currency) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.Orders.create(orderRequest);

        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("id", order.get("id"));
        orderDetails.put("amount", order.get("amount"));
        orderDetails.put("currency", order.get("currency"));

        return orderDetails;
    }

    public boolean verifyAndSavePayment(Map<String, String> paymentData) {
        try {
            String razorpayOrderId = paymentData.get("razorpay_order_id");
            String razorpayPaymentId = paymentData.get("razorpay_payment_id");
            String razorpaySignature = paymentData.get("razorpay_signature");
            int amount = Integer.parseInt(paymentData.get("amount"));
            String currency = paymentData.getOrDefault("currency", "INR");
            int userId = Integer.parseInt(paymentData.get("userId"));

            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec("YOUR_RAZORPAY_SECRET".getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            String generatedSignature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(payload.getBytes()));

            if (generatedSignature.equals(razorpaySignature)) {
                PaymentEntity payment = new PaymentEntity();
                payment.setOrderId(razorpayOrderId);
                payment.setPaymentId(razorpayPaymentId);
                payment.setSignature(razorpaySignature);
                payment.setAmount(amount);
                payment.setCurrency(currency);
                payment.setPaidAt(LocalDateTime.now());
                payment.setStatus("PAID");
                payment.setUserId(userId);

                paymentRepo.save(payment);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<PaymentEntity> getAllPayments() {
        return paymentRepo.findAll();
    }

}
