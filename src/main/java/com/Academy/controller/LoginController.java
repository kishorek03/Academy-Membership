package com.Academy.controller;

import com.Academy.dto.LoginDTO;
import com.Academy.common.LoginMessage;
import com.Academy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<LoginMessage> loginUser(@RequestBody LoginDTO loginDTO) {
        LoginMessage loginMessage = userService.loginUser(loginDTO).getBody();

        // Return the LoginMessage object as part of the ResponseEntity body
        if (loginMessage.isSuccess()) {
            return ResponseEntity.ok(loginMessage);  // Return 200 OK with the success message
        } else {
            // Return BAD_REQUEST status with the error message from the service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginMessage);
        }
    }
}
