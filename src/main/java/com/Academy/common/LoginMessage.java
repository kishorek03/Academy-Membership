package com.Academy.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginMessage {
    private String message;
    private Boolean status; // Status field for any additional information
    private Boolean success; // Indicates success or failure of the operation
    private String token; // Token for authenticated users

    // Constructor for message and success
    public LoginMessage(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Constructor for message, success, and token
    public LoginMessage(String message, boolean success, String token) {
        this.message = message;
        this.success = success;
        this.token = token;
    }
    public boolean isSuccess() {
        return success;
    }
}
