package com.example.newmedicalservice.dto;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "imageTemplates")
public class ImageTemplatesClientDocuments implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Basic
    @Column(name = "templateContract")
    @ToString.Exclude
    private byte[] templateContract;

    @Lob
    @Basic
    @Column(name = "TemplateAgreement")
    @ToString.Exclude
    private byte[] templateAgreement;

    @Lob
    @Basic
    @Column(name = "templateQuestionnaire")
    @ToString.Exclude
    private byte[] templateQuestionnaire;

    @Lob
    @Basic
    @Column(name = "templateClientFoto")
    @ToString.Exclude
    private byte[] templateClientFoto;

}
