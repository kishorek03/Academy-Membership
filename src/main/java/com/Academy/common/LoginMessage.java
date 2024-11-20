package com.Academy.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginMessage {
    private String message;
    private Boolean status;
    private Boolean success;
    // Constructor
    public LoginMessage(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    public boolean isSuccess() {
        return success;
    }
}
