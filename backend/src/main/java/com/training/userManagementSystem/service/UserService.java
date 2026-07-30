package com.training.userManagementSystem.service;

import com.training.userManagementSystem.dto.LoginRequest;
import com.training.userManagementSystem.dto.RegisterRequest;
import com.training.userManagementSystem.dto.UpdateUserRequest;
import com.training.userManagementSystem.dto.UserResponse;
import com.training.userManagementSystem.entity.User;
import com.training.userManagementSystem.exception.BadRequestException;
import com.training.userManagementSystem.exception.ResourceNotFoundException;
import com.training.userManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public UserResponse registerUser(RegisterRequest registerUser) {
        if (userRepository.existsByEmail(registerUser.getEmail())) {
            throw new BadRequestException("Email Is Already Registered");
        }
        User newUser = new User();
        newUser.setName(registerUser.getName());
        newUser.setEmail(registerUser.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));

        User savedUser = userRepository.save(newUser);
        return toResponse(savedUser);
    }

    public UserResponse loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid Email or Password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid Email or Password");
        }

        return toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find User with id " + id));
        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest updateUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find User with id " + id));
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());

        if (updateUser.getPassword() != null && !updateUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find User with id " + id));
        userRepository.delete(existingUser);
    }
}