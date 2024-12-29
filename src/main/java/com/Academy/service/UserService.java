package com.Academy.service;

import com.Academy.common.LoginMessage;
import com.Academy.dto.LoginDTO;
import com.Academy.dto.UserDTO;
import com.Academy.model.User;
import com.Academy.common.JwtUtil;
import com.Academy.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Method to check if an email already exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User registerUser(UserDTO userDTO) {
        // Check if email already exists
        if (emailExists(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Proceed with registration if the email does not exist
        User user = convertToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setResetToken(null); // Ensure resetToken is null on registration


        return userRepository.save(user);
    }

    public User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setGender(userDTO.getGender());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setMobile(userDTO.getMobile());
        user.setUserType(userDTO.getUserType());

        List<User.Children> children = userDTO.getChildren().stream().map(childDTO -> {
            User.Children child = new User.Children();
            child.setName(childDTO.getName());
            child.setAge(childDTO.getAge());
            child.setGender(childDTO.getGender());
            child.setUser(user);
            return child;
        }).collect(Collectors.toList());

        user.setChildren(children);
        return user;
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setGender(user.getGender());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setUserType(user.getUserType());

        List<UserDTO.ChildrenDTO> childrenDTOList = user.getChildren().stream().map(child -> {
            UserDTO.ChildrenDTO childDto = new UserDTO.ChildrenDTO();
            childDto.setId(child.getId());
            childDto.setName(child.getName());
            childDto.setAge(child.getAge());
            childDto.setGender(child.getGender());
            return childDto;
        }).collect(Collectors.toList());

        dto.setChildren(childrenDTOList);
        return dto;
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public ResponseEntity<LoginMessage> loginUser(LoginDTO loginDTO) {
        // Fetch the user by email
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);

        if (user == null) {
            // Email doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LoginMessage("Email not found", false));
        }

        // Validate password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            // Password doesn't match
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginMessage("Invalid email or password", false));
        }

        // Generate token with username and email
        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail());

        // Success
        return ResponseEntity.ok(new LoginMessage("Login successful", true, token));
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }
}
