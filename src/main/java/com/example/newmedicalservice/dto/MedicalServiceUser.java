package com.example.newmedicalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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
    private String UserName;

    @Column(name = "userEmail")
    private String UserEmail;

    @Column(name = "userPhone")
    private String UserTel;

    @Enumerated(EnumType.STRING)
    @Column(name = "userRole", columnDefinition = "ENUM('admin', 'secretary')")
    private UserRole userRole;


    public enum UserRole{
        admin,
        secretary;
    }

}
