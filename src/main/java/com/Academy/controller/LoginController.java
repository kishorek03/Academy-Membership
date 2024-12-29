package com.Academy.controller;

import com.Academy.dto.LoginDTO;
import com.Academy.common.LoginMessage;
import com.Academy.model.User;
import com.Academy.service.UserService;
import com.Academy.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<LoginMessage> loginUser(@RequestBody LoginDTO loginDTO) {
        logger.info("Login request received for email: {}", loginDTO.getEmail());
        LoginMessage loginMessage = userService.loginUser(loginDTO).getBody();

        assert loginMessage != null;
        if (loginMessage.isSuccess()) {
            logger.info("Login successful for email: {}", loginDTO.getEmail());
            return ResponseEntity.ok(loginMessage);
        } else {
            logger.warn("Login failed for email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginMessage);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        logger.info("Forgot password request received for email: {}", email);

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("Email not found in the system: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }

        User user = userOptional.get();
        String token = userService.generateResetToken();
        logger.info("Generated reset token for email: {}", email);

        user.setResetToken(token);
        userService.saveUser(user);
        logger.info("Reset token saved for user: {}", email);

        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendEmail(email, "Reset Your Password", "Click the link to reset your password: " + resetUrl);
        logger.info("Reset password email sent to: {}", email);

        return ResponseEntity.ok("Reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        logger.info("Reset password request received for token: {}", token);

        User user = userService.findByResetToken(token);
        if (user == null) {
            logger.warn("Invalid reset token: {}", token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        userService.updatePassword(user, newPassword);
        logger.info("Password reset successfully for user with email: {}", user.getEmail());

        return ResponseEntity.ok("Password reset successfully.");
    }
}
