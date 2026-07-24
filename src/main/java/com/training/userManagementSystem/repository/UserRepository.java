package com.training.userManagementSystem.repository;


import com.training.userManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //What is the use of Optional used from the Util.Optional Library
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
