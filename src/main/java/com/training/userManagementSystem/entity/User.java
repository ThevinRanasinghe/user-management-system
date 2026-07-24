package com.training.userManagementSystem.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//Creation of User Entity Class

@Entity
@Table(name="users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Name is Required, Please Enter Valid Name")
    @Column(nullable=false)
    private String name;

    @NotBlank(message="Email is required")
    @Email(message="Enter valid email")
    @Column(nullable=false,unique = true)
    private String email;

    @NotBlank(message="Password is required")
    @Size(min=4,message="Password should be length of more than 4 Characters")
    @Column(nullable=false)
    private String password;
}
