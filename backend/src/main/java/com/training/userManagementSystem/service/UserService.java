package com.training.userManagementSystem.service;


import com.training.userManagementSystem.dto.LoginRequest;
import com.training.userManagementSystem.dto.RegisterRequest;
import com.training.userManagementSystem.dto.UpdateUserRequest;
import com.training.userManagementSystem.entity.User;
import com.training.userManagementSystem.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User registerUser(RegisterRequest registerUser){
        if(userRepository.existsByEmail(registerUser.getEmail())){
            throw new IllegalArgumentException("Email Is Already Registered");
        }
        User newUser = new User();
        newUser.setName(registerUser.getName());
        newUser.setEmail(registerUser.getEmail());
        newUser.setPassword(registerUser.getEmail());

        User savedUser = userRepository.save(newUser);
        return savedUser;
    }

    public User loginUser(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email or Password"));

        if(!user.getPassword().equals(loginRequest.getPassword())){
            throw new IllegalArgumentException("Invlaid Email or Password");
        }

        return user;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() ->new IllegalArgumentException("Cannot find User with id " +id));
    }

    public User updateUser(Long id, @Valid UpdateUserRequest updateUser){
        User existingUser = getUserById(id);
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setPassword(updateUser.getPassword());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id){
        User existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }

}
