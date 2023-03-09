package com.example.newmedicalservice.dtoForAnswers;

import com.example.newmedicalservice.dto.Assignment;
import com.example.newmedicalservice.dto.Client;
import com.example.newmedicalservice.dto.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {

    private String assignmentId;

    private String checkupAddress;
    private String checkupMobile;
    private String checkupEmail;
    private String CheckupDescription;

    private LocalDateTime dateTimeAppointment;
    private LocalDateTime dateTimeWhenToDo;
    private String assignmentDescription;
    private String idUser;
    private String clientId;
    private String doctorId;
    private String assignmentType;


}
