package com.Academy.service;

import com.Academy.dto.UserDTO;
import com.Academy.model.User;
import com.Academy.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setGender(userDTO.getGender());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setMobile(userDTO.getMobile());

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

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        return userRepository.save(user);
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setGender(user.getGender());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setMobile(user.getMobile());

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
}
