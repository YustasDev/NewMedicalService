package com.example.newmedicalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doctorArchive")
public class DoctorArchive implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idDoctor")
    private Integer idDoctor;

    @Column(name = "idClient")
    private String idClient;

    @Basic
    @Column(name = "setupDate", updatable = false, nullable = false)
    private LocalDateTime setupDate;

}
