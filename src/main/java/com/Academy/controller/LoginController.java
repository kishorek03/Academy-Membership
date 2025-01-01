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

        // HTML email body with updated sky blue button, gradient, and hover effect
        String emailBody = """
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            body {
                font-family: Arial, sans-serif;
                line-height: 1.6;
                background-color: #f9f9f9;
                color: #333;
                margin: 0;
                padding: 20px;
            }
            .container {
                background-color: #ffffff;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                max-width: 500px;
                margin: auto;
                padding: 20px;
            }
            .header {
                text-align: center;
                color: #4CAF50;
                font-size: 24px;
                margin-bottom: 20px;
            }
            .link {
                text-align: center;
                margin-top: 20px;
            }
            .button {
                display: inline-block;
                background: linear-gradient(90deg, #87CEEB, #00BFFF); /* Sky blue gradient */
                color: white;
                font-weight: bold;
                text-decoration: none;
                padding: 10px;
                border-radius: 4px;
                font-size: 16px;
                text-align: center;
                cursor: pointer;
                transition: background 0.3s ease; /* Smooth transition for hover */
            }
            .button:hover {
                background: linear-gradient(90deg, #00BFFF, #87CEEB); /* Reversed gradient for hover effect */
            }
            .footer {
                margin-top: 20px;
                font-size: 12px;
                color: #777;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">Reset Your Password</div>
            <p>Hello %s,</p> <!-- Dynamic username here -->
            <p>You requested to reset your password. Please click the link below to reset your password:</p>
            <div class="link">
                <a href="%s" class="button">Reset Password</a>
            </div>
            <div class="footer">
                This link is valid for 15 minutes. For security reasons, do not share this email.
            </div>
        </div>
    </body>
    </html>
    """.formatted(user.getName(), resetUrl);  // Replacing %s with user.getName() to personalize

        emailService.sendEmailWithHtml(email, "IMPORTANT: Reset Your Password", emailBody); // Ensure your email service supports HTML
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
