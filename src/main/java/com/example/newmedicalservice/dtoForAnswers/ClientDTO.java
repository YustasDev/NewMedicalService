package com.example.newmedicalservice.dtoForAnswers;

import com.example.newmedicalservice.dto.Assignment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private String id;
    private String passportNumber;
    private String firstName;
    private String surName;
    private String lastName;
    private LocalDateTime registrationDate;
    private Integer familyID;
    private Integer doctorID;
    private String telephone;
    private String email;
    private String address;
    //    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name="paymentPlan_id")
//    private PaymentPlan paymentPlan;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime startPaymentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime startServiceDate;

    private String serviceDescription;
    private Boolean blocked;
    private String blockedReasonDescription;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime blockDate;
    private Long clientDocsID;
    private String kxNumber;
    private List<AssignmentDTO> assignmentList;

}
