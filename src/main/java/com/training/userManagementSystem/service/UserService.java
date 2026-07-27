package com.training.userManagementSystem.service;


import com.training.userManagementSystem.dto.LoginRequest;
import com.training.userManagementSystem.dto.RegisterRequest;
import com.training.userManagementSystem.entity.User;
import com.training.userManagementSystem.exception.BadRequestException;
import com.training.userManagementSystem.exception.ResourceNotFoundException;
import com.training.userManagementSystem.repository.UserRepository;
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

    public User registerUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new BadRequestException("Email Is Already Registered");
        }

        User registerUser = new User();
        registerUser.setName(request.getName());
        registerUser.setEmail(request.getEmail());
        registerUser.setPassword(request.getPassword());

        return userRepository.save(registerUser);
    }

    public User loginUser(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email not Found"));

        if(!user.getPassword().equals(password)){
            throw new BadRequestException("Invlaid Password for the given Email");
        }

        return user;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() ->new ResourceNotFoundException("Cannot find User with id " +id));
    }

    public User updateUser(Long id, User updateUser){
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
