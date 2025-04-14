package com.Academy.service;

import com.Academy.dto.UserDTO;
import com.Academy.model.User;
import com.Academy.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;
    public UserDTO getUserDetailsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return convertToUserDTO(user);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setMobile(user.getMobile());
        dto.setUserType(user.getUserType());
        return dto;
    }
}
