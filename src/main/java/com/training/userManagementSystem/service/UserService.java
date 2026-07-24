package com.training.userManagementSystem.service;


import com.training.userManagementSystem.entity.User;
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
            throw new IllegalArgumentException("Email Is Already Registered");
        }
        return userRepository.save(user);
    }

    public User loginUser(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email or Password"));

        if(!user.getPassword().equals(password)){
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
