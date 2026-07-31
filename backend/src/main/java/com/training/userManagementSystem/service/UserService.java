package com.training.userManagementSystem.service;

import com.training.userManagementSystem.dto.LoginRequest;
import com.training.userManagementSystem.dto.RegisterRequest;
import com.training.userManagementSystem.dto.UpdateUserRequest;
import com.training.userManagementSystem.dto.UserResponse;
import com.training.userManagementSystem.entity.User;
import com.training.userManagementSystem.exception.BadRequestException;
import com.training.userManagementSystem.exception.ResourceNotFoundException;
import com.training.userManagementSystem.repository.UserRepository;
import com.training.userManagementSystem.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), null);
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

        String token = jwtUtil.generateToken(user.getEmail());
        UserResponse response = toResponse(user);
        response.setToken(token);
        return response;
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponse);
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

    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
                .map(this::toResponse);
    }
}