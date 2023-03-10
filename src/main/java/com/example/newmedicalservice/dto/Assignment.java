package com.example.newmedicalservice.dto;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "assignment")
public class Assignment implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "checkupAddress", nullable = false)
    @NotBlank(message="CheckupAddress is required")
    private String checkupAddress;


    @Column(name = "checkupMobile", nullable = false)
    @NotBlank(message="CheckupMobile is required")
    private String checkupMobile;

    @Column(name = "checkupEmail", nullable = false)
    @NotBlank(message="CheckupEmail is required")
    private String checkupEmail;

    @Column(name = "checkupDescription", nullable = false)
    @NotBlank(message="CheckupDescription is required")
    private String CheckupDescription;

    @Basic
    @Column(name = "dateTimeAppointment", nullable = false)
    @NotBlank(message="DateTimeAppointment is required")
    private LocalDateTime dateTimeAppointment;

    @Basic
    @Column(name = "dateTimeWhenToDo", nullable = false)
    @NotBlank(message="DateTimeWhenToDo is required")
    private LocalDateTime dateTimeWhenToDo;

    @Column(name = "assignmentDescription", nullable = false)
    @NotBlank(message="AssignmentDescription is required")
    private String assignmentDescription;

    @Column(name = "idUser", nullable = false)
    @NotBlank(message="MedicalServiceUser is required")
    private Integer idUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_id")
    @ToString.Exclude
    private Doctor doctor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="client_id")
    @ToString.Exclude
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Type1', 'Type2', 'Type3')")
    private AssignmentType assignmentType;

    @Basic
    @Column(name = "isDone", nullable = false)
    private Boolean isDone;


    public enum AssignmentType{
        Type1,
        Type2,
        Type3;
    }
}
