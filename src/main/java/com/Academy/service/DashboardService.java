package com.Academy.service;

import com.Academy.dto.UserDTO;
import com.Academy.model.User;
import com.Academy.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        userDTO.setProfilePictureUrl(user.getProfilePictureUrl()); // Include profile picture URL
        return userDTO;
    }

    // Upload profile picture for a user
    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Ensure the file is not empty
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // Generate a unique file name and save to a specified path
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = "uploads/profile_pictures/" + fileName;

            File destinationFile = new File(filePath);
            // Create directories if they don't exist
            destinationFile.getParentFile().mkdirs();

            // Save the file
            file.transferTo(destinationFile);

            // Update the user's profile picture URL in the database
            user.setProfilePictureUrl(filePath);
            userRepository.save(user);

            return "Profile picture uploaded successfully: " + filePath;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Update the profile picture URL in the database
    public void updateUserProfilePicture(Long userId, String profilePictureUrl) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Update the profile picture URL
            user.setProfilePictureUrl(profilePictureUrl);
            // Save the updated user entity back to the database
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with userId: " + userId);
        }
    }
}
