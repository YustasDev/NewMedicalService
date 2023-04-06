package com.example.newmedicalservice.dtoForAnswers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireDTO {

    private String clientID;
    private String birthdayDate;
    private Double weight;
    private Double height;
    private String transferredOperations;
    private String hospitalizations;

    private String diabetes;
    private String highBloodPressure;
    private String elevatedBloodCholesterol;
    private String cardiovascularDiseases;
    private String increasedWeight;
    private String otherChronic;

    private String smoking;
    private Boolean alcohol;
    private Boolean drugs;

    private String familyOncology;
    private String familyCardiovascular;
    private String familyDiseases;
    private String familyDiabetes;
    private String familyPressure;
    private String familyHeartAttacksStrokes;
    private String listOfMedicines;
    private String complaints;




}
