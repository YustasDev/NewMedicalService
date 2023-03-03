package com.example.newmedicalservice.dtoForAnswers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDocsDTO {

    private Long id;
    private String clientID;
    private String passportNumber;

    private Boolean contract;
    private Boolean agreement;
    private Boolean questionnaire;
    private Boolean clientFoto;

}

