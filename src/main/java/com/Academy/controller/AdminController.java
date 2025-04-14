package com.Academy.controller;

import com.Academy.model.PaymentEntity;
import com.Academy.model.User;
import com.Academy.model.Announcement;
import com.Academy.service.UserService;
import com.Academy.service.AnnouncementService;
import com.Academy.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private PaymentService feePaymentService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        logger.info("Updating user with ID: {}", id);
        User user = userService.updateUser(id, updatedUser);
        logger.info("User with ID: {} updated successfully", id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        logger.info("User with ID: {} deleted successfully", id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id,
                                               @RequestParam boolean active) {
        logger.info("Updating status for user ID: {} to active: {}", id, active);
        userService.updateActiveStatus(id, active);
        logger.info("Status updated for user ID: {}", id);
        return ResponseEntity.ok("Status updated");
    }

    // ---------- ANNOUNCEMENTS ----------
    @PostMapping("/postAnnouncement")
    public ResponseEntity<String> createAnnouncement(@RequestBody Announcement announcement) {
        logger.info("Creating new announcement: {}", announcement.getTitle());
        announcementService.saveAnnouncement(announcement);
        logger.info("Announcement '{}' created successfully", announcement.getTitle());
        return ResponseEntity.ok("Announcement created successfully.");
    }

    @GetMapping("/announcements")
    public List<Announcement> getAllAnnouncements() {
        logger.info("Fetching all announcements");
        return announcementService.getAllAnnouncements();
    }

    @GetMapping("/announcement/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable Long id) {
        logger.info("Fetching announcement with ID: {}", id);
        Optional<Announcement> announcement = announcementService.getAnnouncementById(id);
        return announcement.map(a -> {
            logger.info("Announcement with ID: {} found", id);
            return ResponseEntity.ok(a);
        }).orElseGet(() -> {
            logger.warn("Announcement with ID: {} not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @PutMapping("/announcement/{id}")
    public ResponseEntity<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        logger.info("Updating announcement with ID: {}", id);
        Optional<Announcement> existingAnnouncement = announcementService.getAnnouncementById(id);
        if (existingAnnouncement.isPresent()) {
            announcement.setId(id);
            announcementService.saveAnnouncement(announcement);
            logger.info("Announcement with ID: {} updated successfully", id);
            return ResponseEntity.ok("Announcement updated successfully.");
        } else {
            logger.warn("Announcement with ID: {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/announcement/{id}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long id) {
        logger.info("Deleting announcement with ID: {}", id);
        Optional<Announcement> existingAnnouncement = announcementService.getAnnouncementById(id);
        if (existingAnnouncement.isPresent()) {
            announcementService.deleteAnnouncement(id);
            logger.info("Announcement with ID: {} deleted", id);
            return ResponseEntity.ok("Announcement deleted successfully.");
        } else {
            logger.warn("Announcement with ID: {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
    }

    // ---------- FEE PAYMENTS ----------
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentEntity>> getAllFeePayments() {
        logger.info("Fetching all fee payments");
        List<PaymentEntity> payments = feePaymentService.getAllPayments();
        logger.info("Total payments fetched: {}", payments.size());
        return ResponseEntity.ok(payments);
    }
}
