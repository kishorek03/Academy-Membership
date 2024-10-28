package com.Academy.controller;

import com.Academy.model.User;
import com.Academy.dto.UserDTO;
import com.Academy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userService.convertToEntity(userDTO);
            User registeredUser = userService.registerUser(user);
            UserDTO registeredUserDTO = userService.toUserDTO(registeredUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUserDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.findUserById(id);

        if (userOptional.isPresent()) {
            UserDTO userDto = userService.toUserDTO(userOptional.get());
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
