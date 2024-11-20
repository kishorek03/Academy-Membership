package com.Academy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final long EXPIRE_MINUTES = 3;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generateOtp(String email) {
        String existingOtp = redisTemplate.opsForValue().get(email);
        if (existingOtp != null) {
            return "OTP already sent!..Please check your email.";
        }
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        redisTemplate.opsForValue().set(email, otp, EXPIRE_MINUTES, TimeUnit.MINUTES);

        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String cachedOtp = redisTemplate.opsForValue().get(email);
        return otp.equals(cachedOtp);
    }

    public void clearOtp(String email) {
        redisTemplate.delete(email);
    }
}
