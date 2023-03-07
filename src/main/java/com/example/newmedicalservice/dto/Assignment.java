//package com.example.newmedicalservice.dto;
//
//import lombok.*;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//@Table(name = "assignment")
//public class Assignment implements Serializable {
//
//    @Id
//    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "assignmentType", nullable = false)
//    @NotBlank(message="AssignmentType is required")
//    private String assignmentType;
//
//    @Column(name = "assignmentPlace", nullable = false)
//    @NotBlank(message="AssignmentPlace is required")
//    private String assignmentPlace;
//
//    @Basic
//    @Column(name = "dateTimeAppointment", nullable = false)
//    @NotBlank(message="DateTimeAppointment is required")
//    private LocalDateTime dateTimeAppointment;
//
//    @Basic
//    @Column(name = "dateTimeWhenToDo", nullable = false)
//    @NotBlank(message="DateTimeWhenToDo is required")
//    private LocalDateTime dateTimeWhenToDo;
//
//    @Column(name = "assignmentDescription", nullable = false)
//    @NotBlank(message="AssignmentDescription is required")
//    private String assignmentDescription;
//
//    @Column(name = "idUser", nullable = false)
//    @NotBlank(message="MedicalServiceUser is required")
//    private Integer idUser;
//
//
//
//
//
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name="client_id")
//    @ToString.Exclude
//    private Client client;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "doctor_id")
//    @ToString.Exclude
//    private Doctor doctor;
//
//
//
//
//    IDDoctorTarget
//            IDCheckupsTarget
//
//
//
//
//
//
//
//}
