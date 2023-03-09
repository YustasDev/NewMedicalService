package com.example.newmedicalservice.dtoForAnswers;

import com.example.newmedicalservice.dto.Assignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private Integer id;
    private List<ClientDTO> clientList;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorSureName;
    private String doctorTelefon;
    private String doctorEmail;
    private String doctorAddres;
    private String doctorType;
    private String description;
    private List<AssignmentDTO> assignmentList;
}
