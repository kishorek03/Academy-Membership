package com.Academy.controller;

import com.Academy.dto.UserDTO;
import com.Academy.model.User;
import com.Academy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "http://localhost:3000")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Received request to register user with email: {}", userDTO.getEmail());
        try {
            // Check if email already exists
            if (userService.emailExists(userDTO.getEmail())) {
                // Return a BAD_REQUEST with a specific message if the email is already in use
                logger.error("Registration failed for email {}: User already exists", userDTO.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with this email already exists");
            }

            // Proceed with registration if the email is not taken
            User registeredUser = userService.registerUser(userDTO);
            UserDTO registeredUserDTO = userService.toUserDTO(registeredUser);
            logger.info("User registered successfully with ID: {}", registeredUser.getId());

            // Send a successful response
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUserDTO);

        } catch (IllegalArgumentException e) {
            // Handle other errors (like validation errors or server-side issues)
            logger.error("Registration failed for email {}: {}", userDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle general errors that might occur during the registration process
            logger.error("An unexpected error occurred during registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later.");
        }
    }
    @PostMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean emailExists = userService.emailExists(email);

        if (emailExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already registered. Please use a different email.");
        }
        return ResponseEntity.ok("Email is available.");
    }
    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        logger.info("Received request to generate OTP for email: {}", email);
        try {
            String response = otpService.generateOtp(email);
            if (response.equals("OTP already sent. Please check your email.")) {
                logger.warn("OTP generation attempt for email {}: {}", email, response);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            emailService.sendOtpEmail(email, response);
            logger.info("OTP generated and sent to email: {}", email);
            return ResponseEntity.ok("OTP sent to email.");
        } catch (Exception e) {
            logger.error("Failed to generate OTP for email {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
        }
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String otp = requestBody. get("otp");
        logger.info("Received OTP verification request for email: {}", email);
        if (otpService.validateOtp(email, otp)) {
            otpService.clearOtp(email);
            logger.info("OTP verified successfully for email: {}", email);
            return ResponseEntity.ok("OTP verified. Proceed with registration.");
        } else {
            logger.warn("OTP verification failed for email: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        try {
            User user = userService.findUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            logger.info("User found with ID: {}", id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
