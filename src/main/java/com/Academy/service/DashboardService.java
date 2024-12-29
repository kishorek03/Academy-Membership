package com.Academy.service;

import com.Academy.dto.UserDTO;
import com.Academy.model.User;
import com.Academy.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    // Fetch user profile details by userId
    public UserDTO getUserProfile(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return convertToUserDTO(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Convert User entity to UserDTO
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setGender(user.getGender());
        userDTO.setEmail(user.getEmail());
        userDTO.setMobile(user.getMobile());
        userDTO.setUserType(user.getUserType());
        // Add any additional data you need to include in the DTO
        return userDTO;
    }

    // Upload profile picture for a user
    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Save the file (you can implement custom logic to save the file in your desired location)
            String fileName = file.getOriginalFilename();
            String filePath = "/path/to/save/" + fileName;

            // Assuming you save the file at a specified path
            file.transferTo(new java.io.File(filePath));

            // Save the file path to user profile (you can add a field for profilePicture in the User entity)
            user.setProfilePicture(filePath); // Assuming you have a setProfilePicture method in User class
            userRepository.save(user);

            return "Profile picture uploaded successfully";
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
