package com.Academy.controller;

import com.Academy.dto.UserDTO;
import com.Academy.model.Announcement;
import com.Academy.model.User;
import com.Academy.repo.UserRepository;
import com.Academy.service.AnnouncementService;
import com.Academy.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
@Autowired
    private final ProfileService profileService;
@Autowired
    private AnnouncementService announcementService;


    @Autowired
    private UserRepository userRepository;
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getLoggedInUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthenticated request to /profile/me");
            return ResponseEntity.status(401).body(null);  // Unauthorized
        }
        String username = authentication.getName();
        logger.info("Fetching profile for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        logger.debug("User found: email = {}", user.getEmail());
        UserDTO userDTO = profileService.getUserDetailsByEmail(user.getEmail());
        if (userDTO == null) {
            logger.warn("UserDTO conversion failed for email: {}", user.getEmail());
            return ResponseEntity.status(404).body(null);
        }
        logger.info("Returning profile details for: {}", userDTO.getEmail());
        return ResponseEntity.ok(userDTO);
    }
    @GetMapping("/announcements")
    public List<Announcement> getAllAnnouncements() {
        logger.info("Fetching all announcements");
        return announcementService.getAllAnnouncements();
    }
}
