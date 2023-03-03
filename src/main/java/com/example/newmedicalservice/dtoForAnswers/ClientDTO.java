package com.example.newmedicalservice.dtoForAnswers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


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
    //    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name="paymentPlan_id")
//    private PaymentPlan paymentPlan;
    private LocalDateTime startPaymentDate;
    private LocalDateTime startServiceDate;
    private String serviceDescription;
    private Boolean blocked;
    private String blockedReasonDescription;
    private LocalDateTime blockDate;
    private Long clientDocsID;
    private String kxNumber;

}
