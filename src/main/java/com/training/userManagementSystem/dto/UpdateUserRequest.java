package com.training.userManagementSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank(message = "Name is Required")
    private String name;

    @NotBlank(message = "Email is Required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be Empty")
    @Size(min = 4,max = 15, message = "Password should be atleast 4 characters long")
    private String password;
}
