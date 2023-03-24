package com.example.newmedicalservice.dto;


import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class StatusDocAnswer {
    public Boolean contract;
    public Boolean agreement;
    public Boolean questionnaire;
    public Boolean foto;

    public StatusDocAnswer() {
        this.contract = false;
        this.agreement = false;
        this.questionnaire = false;
        this.foto = false;
    }
}
