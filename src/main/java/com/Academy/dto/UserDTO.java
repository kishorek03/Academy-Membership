package com.Academy.dto;

import com.Academy.common.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{7,}$",
            message = "Password must contain at least one letter, one number, and be at least 7 characters long")
    private String password;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number should be exactly 10 digits")
    private String mobile;

    private UserType userType;

    private String profilePictureUrl;

    private List<ChildrenDTO> children;  // Only applicable for parents

    private PaymentDTO payment;  // Payment should be a separate object

    @Data
    public static class ChildrenDTO {

        private Long id;

        @NotBlank(message = "Child's name is required")
        private String name;

        @NotBlank(message = "Child's age is required")
        @Pattern(regexp = "^\\d+$", message = "Age should be a numeric value")
        private String age;

        @NotBlank(message = "Child's gender is required")
        private String gender;

        private PaymentDTO payment;  // Payment details for the child (Player)
    }

    @Data
    public static class PaymentDTO {
        private double amount;
        private String currency;
    }
}
