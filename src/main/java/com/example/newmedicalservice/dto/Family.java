package com.example.newmedicalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "family")
public class Family implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "family")
    private List<Client> clientList;

    @Column(name = "familyName")
    private String familyName;

    @Column(name = "familyHead")
    private String FamilyHead;

    @Column(name = "familyMobile", unique = true)
    private String familyMobile;

    @Column(name = "description")
    private String description;
}
