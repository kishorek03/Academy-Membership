package com.Academy.controller;
import com.Academy.dto.UserDTO;
import com.Academy.service.DashboardService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("")
public class DashboardController {

    private final DashboardService dashboardService;
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    // Directory for storing uploaded files
    private static final String UPLOAD_DIR = "uploads/profile-pictures/";

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Get user profile
    @GetMapping("/profile/{userId}")
    public UserDTO getUserProfile(@PathVariable Long userId) {
        logger.info("Fetching profile for userId: {}", userId);
        return dashboardService.getUserProfile(userId);
    }

    // Upload profile picture
    @PostMapping("/profile/upload/{userId}")
    public String uploadProfilePicture(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        logger.info("Received request to upload profile picture for userId: {}", userId);

        // Create the directory if it doesn't exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Created directory for uploads at: {}", UPLOAD_DIR);
            } else {
                logger.error("Failed to create directory for uploads.");
                return "Error: Could not create upload directory.";
            }
        }

        try {
            // Generate a unique file name
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                logger.error("Uploaded file has no name.");
                return "Error: Invalid file.";
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uniqueFilename = "user-" + userId + System.currentTimeMillis() + extension;

            // Save the file
            Path filePath = Paths.get(UPLOAD_DIR, uniqueFilename);
            Files.write(filePath, file.getBytes());
            logger.info("File uploaded successfully: {}", filePath);

            // Save the file path in the database
            String profilePictureUrl = "/dashboard/profile-pictures/" + uniqueFilename; // URL for serving the file
            dashboardService.updateUserProfilePicture(userId, profilePictureUrl);

            logger.info("Profile picture URL saved: {}", profilePictureUrl);
            return profilePictureUrl;
        } catch (IOException e) {
            logger.error("Error uploading file for userId {}: {}", userId, e.getMessage());
            return "Error uploading file: " + e.getMessage();
        }
    }
}
