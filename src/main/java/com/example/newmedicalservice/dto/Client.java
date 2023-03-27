package com.example.newmedicalservice.dto;

import com.example.newmedicalservice.security.AttributeEncryptor;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "client")
public class Client implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;


    @Column(name = "passportNumber", nullable = false, unique=true)
    @NotBlank(message="Passport number is required")
    private String passportNumber;

    @Column(name = "firstName", nullable = false)
    @NotBlank(message="First name is required")
    private String firstName;

    @Column(name = "surname", nullable = false)
    @NotBlank(message="Surname is required")
    private String surName;

    @Column(name = "lastName")
    private String lastName;

    @Basic
    @Column(name = "registrationDate", updatable = false, nullable = false)
    private LocalDateTime registrationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="family_id")
    @ToString.Exclude
    private Family family;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="doctor_id")
    @ToString.Exclude
    private Doctor doctor;

    @Column(name = "telephon")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

//    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name="paymentPlan_id")
//    private PaymentPlan paymentPlan;

    @Basic
    @Column(name = "startPaymentDate")
    private LocalDateTime startPaymentDate;

    @Basic
    @Column(name = "startServiceDate")
    private LocalDateTime startServiceDate;

    @Column(name = "serviceDescription")
    private String serviceDescription;

    @Column(columnDefinition = "boolean default false")
    private Boolean blocked;

    @Column(name = "blockedReasonDescription")
    private String blockedReasonDescription;

    @Basic
    @Column(name = "blockDate")
    private LocalDateTime blockDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "clientDocs_id", referencedColumnName = "id")
    @ToString.Exclude
    private ClientDocs clientDocs;

    @Convert(converter = AttributeEncryptor.class)
    @Column(name = "kxNumber")
    private String kxNumber;

    @OneToMany(mappedBy = "client")
    private List<Assignment> assignmentList;

}
