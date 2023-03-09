package com.example.newmedicalservice.dtoForAnswers;

import com.example.newmedicalservice.dto.MedicalServiceUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceUserDTO {

    private String userId;
    private String userLogin;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userTel;
    private String userRole;

}
