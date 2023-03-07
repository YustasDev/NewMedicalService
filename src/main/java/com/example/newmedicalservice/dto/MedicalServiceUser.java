package com.example.newmedicalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class MedicalServiceUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "login", nullable = false,  unique=true)
    @NotBlank(message="Login is required")
    private String login;

    @Column(name = "password", nullable = false,  unique=true)
    @NotBlank(message="Password is required")
    private String password;

    @Column(name = "userName")
    private String userName;

    @Column(name = "userEmail")
    private String userEmail;

    @Column(name = "userPhone")
    private String userTel;

    @Enumerated(EnumType.STRING)
    @Column(name = "userRole", columnDefinition = "ENUM('admin', 'secretary')", nullable = false)
    private UserRole userRole;


    public enum UserRole{
        admin,
        secretary;
    }

}
