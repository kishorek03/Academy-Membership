package com.Academy.service;

import com.Academy.common.JwtUtil;
import com.Academy.common.LoginMessage;
import com.Academy.dto.LoginDTO;
import com.Academy.dto.UserDTO;
import com.Academy.model.User;
import com.Academy.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );
    }
    public User registerUser(UserDTO userDTO) {
        if (emailExists(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = convertToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setResetToken(null);

        return userRepository.save(user);
    }
    public ResponseEntity<LoginMessage> loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LoginMessage("Email not found", false));
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginMessage("Invalid email or password", false));
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(),user.getId(),user.getRole().name());
        return ResponseEntity.ok(new LoginMessage("Login successful", true, token));
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

    // ✅ 6. Password reset & utility functions
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setMobile(updatedUser.getMobile());
        user.setGender(updatedUser.getGender());
        user.setUserType(updatedUser.getUserType());
        return userRepository.save(user);
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateActiveStatus(Long id, boolean active) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setActive(active);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }

}
