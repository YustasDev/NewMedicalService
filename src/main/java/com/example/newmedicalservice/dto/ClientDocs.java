package com.example.newmedicalservice.dto;

import com.example.newmedicalservice.dtoForAnswers.QuestionnaireDTO;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientDocs")
public class ClientDocs implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "clientDocs")
    private Client client;

    @Column(name = "passportNumber", nullable = false)
    @NotBlank(message="Passport number is required")
    private String passportNumber;

    // https://stackoverflow.com/questions/25738569/how-to-map-a-map-json-column-to-java-object-with-jpa
//    @Column(name = "contract", columnDefinition = "json")
//    @JsonRawValue
//    private String contract;
//
//    @Column(name = "agreement", columnDefinition = "json")
//    @JsonRawValue
//    private String agreement;
//
//    @Column(name = "questionnaire", columnDefinition = "json")
//    private String questionnaire;

    // https://stackoverflow.com/questions/42875899/create-mysql-table-with-json-column-inside-spring-boot-hibernate-schema-valida
    @Column(name = "questionnaire", columnDefinition = "json")
    @ToString.Exclude
    private String questionnaire;


    @Lob
    @Basic
    @Column(name = "contract")
    @ToString.Exclude
    private byte[] contract;

    @Column(name = "contract_signature")
    private Boolean contractSignature;

    @Lob
    @Basic
    @Column(name = "agreement")
    @ToString.Exclude
    private byte[] agreement;

    @Column(name = "agreement_signature")
    private Boolean agreementSignature;



    @Lob
    @Basic
    @Column(name = "clientFoto")
    @ToString.Exclude
    private byte[] clientFoto;
}
