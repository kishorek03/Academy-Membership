package com.Academy.controller;

import com.Academy.dto.UserDTO;
import com.Academy.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // Fetch user profile details
    @GetMapping("/profile/{userId}")
    public UserDTO getUserProfile(@PathVariable Long userId) {
        return dashboardService.getUserProfile(userId);
    }

    // Upload profile picture
    @PostMapping("/profile/upload/{userId}")
    public String uploadProfilePicture(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            return dashboardService.uploadProfilePicture(userId, file);
        } catch (IOException e) {
            return "Error uploading file: " + e.getMessage();
        }
    }

    // Fetch announcements
    @GetMapping("/announcements")
    public List<String> getAnnouncements() {
        return List.of("Table tennis tournament coming soon!", "New membership packages available.");
    }
}
