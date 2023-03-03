package com.example.newmedicalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doctor")
public class Doctor implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "doctor")
    private List<Client> clientList;

    @Column(name = "doctorFirstName", nullable = false)
    @NotBlank(message="DoctorFirstName is required")
    private String doctorFirstName;

    @Column(name = "doctorLastName", nullable = false)
    @NotBlank(message="DoctorLastName is required")
    private String doctorLastName;

    @Column(name = "doctorSureName", nullable = false)
    @NotBlank(message="DoctorSureName is required")
    private String doctorSureName;

    @Column(name = "doctorTelefon", nullable = false)
    @NotBlank(message="Doctor telefon is required")
    private String doctorTelefon;

    @Column(name = "doctorEmail", nullable = false)
    @NotBlank(message="Doctor e-mail is required")
    private String doctorEmail;

    @Column(name = "doctorAddres", nullable = false)
    @NotBlank(message="Doctor addres is required")
    private String doctorAddres;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('AAA', 'BBB', 'CCC')")
    private DoctorType doctorType;

    @Column(name = "description", nullable = false)
    @NotBlank(message="Description is required")
    private String description;


    public enum DoctorType{
        AAA,
        BBB,
        CCC;
    }

}
