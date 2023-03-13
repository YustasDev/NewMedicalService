package com.example.newmedicalservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
    private String checkupDescription;


    @Basic
    @Column(name = "dateTimeAppointment", nullable = false)
    private LocalDateTime dateTimeAppointment;

    @Basic
    @Column(name = "dateTimeWhenToDo", nullable = false)
    private LocalDateTime dateTimeWhenToDo;

    @Column(name = "assignmentDescription", nullable = false)
    @NotBlank(message="AssignmentDescription is required")
    private String assignmentDescription;

    @Column(name = "idUser", nullable = false)
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
