package com.example.newmedicalservice.controllers;


import com.example.newmedicalservice.dto.*;
import com.example.newmedicalservice.dtoForAnswers.*;
import com.example.newmedicalservice.repository.ClientRepository;
import com.example.newmedicalservice.repository.DoctorArchiveRepository;
import com.example.newmedicalservice.repository.DoctorRepository;
import com.example.newmedicalservice.repository.FamilyRepository;
import com.example.newmedicalservice.service.ClientService;
import com.example.newmedicalservice.service.DefaultEmailService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/customers")
public class ClientController {

    private static final Logger LOGGER = LogManager.getLogger(ClientController.class);
    final static String REGEXEMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";


    private ClientRepository clientRepository;
    private ClientService clientService;
    private FamilyRepository familyRepository;
    private DefaultEmailService emailService;
    private DoctorRepository doctorRepository;
    private DoctorArchiveRepository doctorArchiveRepository;

    @Autowired
    public ClientController(ClientRepository clientRepository, ClientService clientService,
                            FamilyRepository familyRepository, DefaultEmailService emailService,
                            DoctorRepository doctorRepository, DoctorArchiveRepository doctorArchiveRepository) {
        this.clientRepository = clientRepository;
        this.clientService = clientService;
        this.familyRepository = familyRepository;
        this.emailService = emailService;
        this.doctorRepository = doctorRepository;
        this.doctorArchiveRepository = doctorArchiveRepository;
    }


//    @CrossOrigin
//    @PostMapping("/getDocument")
//    ResponseEntity<?> returnDocument(@RequestParam(name="clientID", required=true) String id,
//                                     @RequestParam(name="documentType", required=true) String document) {
//
//    }

    @CrossOrigin
    @PostMapping("/getDocumentsStatus")
    ResponseEntity<?> returnDocument(@RequestParam(name="clientID") String id) {
        StatusDocAnswer statusDocAnswer = clientService.getStatusClientDocuments(id);
        if(statusDocAnswer != null){
            LOGGER.info("Client status received with id = '" + id + "'  ==> " + statusDocAnswer.toString());
            return ResponseEntity.status(HttpStatus.OK).body(statusDocAnswer);
        }
        else {
            LOGGER.error("A request for the status of the filled documents has been fulfilled. There is no such client with ID = " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such client with ID = " + id);
        }
    }



    @CrossOrigin
    @GetMapping("/getFamily")
    ResponseEntity<?> getFamily(){
        List<FamilyDTO> familyDTOList = clientService.getFamilyList();
        return ResponseEntity.status(HttpStatus.OK).body(familyDTOList);
    }

    @CrossOrigin
    @PatchMapping("/modifyFamily")
    ResponseEntity<?> modifyFamilyData(@RequestParam(name="familyID", required=true) String familyID,
                                       @RequestParam(name="familyName", required=false) String familyName,
                                       @RequestParam(name="familyMobile", required=false) String familyMobile,
                                       @RequestParam(name="familyDescription", required=false) String familyDescription,
                                       @RequestParam(name="familyHead", required=false) String familyHead){

    Family familyAfterRedact = clientService.redactFamilyData(familyID, familyName, familyMobile, familyDescription, familyHead);
        if(familyAfterRedact != null) {
            FamilyDTO familyDTO = clientService.mapFamily_toFamilyDTO(familyAfterRedact);
            return ResponseEntity.status(HttpStatus.CREATED).body(familyDTO);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such family with ID = " + familyID);
        }
    }


    @CrossOrigin
    @PostMapping("/createNewClient")
    ResponseEntity<?> setClient(@RequestBody ClientDTO clientDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName_inService = authentication.getName();
        Marker marker = clientService.getLogMarker();

        Client newClient = new Client();
        newClient.setPassportNumber(clientDTO.getPassportNumber());
        newClient.setFirstName(clientDTO.getFirstName());
        newClient.setSurName(clientDTO.getSurName());
        newClient.setLastName(clientDTO.getLastName());
        newClient.setKxNumber(clientDTO.getKxNumber());

        String phone = clientDTO.getTelephone();
        if(phone != null) {
            phone = phone.replaceAll("[\\D]", "");
            if (phone.length() == 11) {
                newClient.setTelephone(clientDTO.getTelephone());
            } else {
                System.err.println("The phone number is not valid");
                LOGGER.error(marker, "The phone number given is invalid: " + phone);
            }
        }
        String clientEmail = clientDTO.getEmail();
        if (clientEmail.matches(REGEXEMAIL)) {
            newClient.setEmail(clientEmail);
        } else {
            System.err.println("The client e-mail is not valid");
            LOGGER.error(marker, "The client e-mail given is invalid: " + clientEmail);
        }

        Doctor doctor = null;
        if(clientDTO.getDoctorID() != null) {
            Optional<Doctor> doctorOptional = doctorRepository.findById(clientDTO.getDoctorID());
            if (doctorOptional.isPresent()) {
                doctor = doctorOptional.get();
                newClient.setDoctor(doctor);
            }
            else {
                LOGGER.error(marker, "Doctor with ID = '" + clientDTO.getDoctorID() + "' does not exist in the database");
            }
        }

        newClient.setStartPaymentDate(clientDTO.getStartPaymentDate());
        newClient.setStartServiceDate(clientDTO.getStartServiceDate());
        newClient.setServiceDescription(clientDTO.getServiceDescription());
        newClient.setBlocked(clientDTO.getBlocked());
        newClient.setBlockedReasonDescription(clientDTO.getBlockedReasonDescription());
        newClient.setBlockDate(clientDTO.getBlockDate());

        Family family = null;

        newClient.setRegistrationDate(LocalDateTime.now());
        ClientDocs clientDocs = new ClientDocs();
        clientDocs.setPassportNumber(clientDTO.getPassportNumber());
        newClient.setClientDocs(clientDocs);
        if(clientDTO.getFamilyID() != null){
            family = familyRepository.getReferenceById(clientDTO.getFamilyID());
        }
        else {
            family = new Family();
            family.setFamilyMobile(newClient.getTelephone());
        }
        newClient.setFamily(family);
        ClientDTO clientForAnswer = null;
        try {
            clientRepository.save(newClient);
            clientForAnswer = clientService.mapClient_toClientDTOclass(newClient);

            /* archive the doctor's assignment data */
            if(clientDTO.getDoctorID() != null && doctor != null){
                DoctorArchive doctorArchive = new DoctorArchive();
                doctorArchive.setIdDoctor(doctor.getId());
                doctorArchive.setIdClient(newClient.getId());
                doctorArchive.setSetupDate(LocalDateTime.now());
                doctorArchiveRepository.save(doctorArchive);
            }
        }
        catch (ConstraintViolationException | DataIntegrityViolationException cve ){
            LOGGER.error(marker, "An error occurred when creating a new customer ==> " + cve);
            LOGGER.error(marker, "Incoming data to create a new client ==> " + clientDTO);
            String errorAnswer = "";
            String errorMessage = cve.getMessage();
            if(errorMessage.contains("messageTemplate")) {
                errorAnswer = errorMessage.substring(errorMessage.lastIndexOf("messageTemplate"))
                        .replaceAll("[\\p{Punct}\\s&&[^\\h]&&[^-]]", "").replaceAll("messageTemplate", "");
            }
            errorAnswer = "The limitations of the data schema are violated";
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorAnswer);
        }
        catch (Exception e){
            LOGGER.error(marker, e.getMessage());
            LOGGER.error(marker, "Incoming data to create a new client ==> " + clientDTO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("internal error");
        }

        LOGGER.info(marker, "There is currently a user in the service: '" + currentUserName_inService + "'");
        LOGGER.info(marker, "User: '" + currentUserName_inService + "' created a new Client with ID Number = " + newClient.getId());

        try {
            emailService.sendEmail(newClient.getEmail(), newClient.getId());
        } catch (MessagingException | FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.error(marker, "Error occurred when sending an email: " + e);
        }
        LOGGER.info(marker, "Client with passport number " + newClient.getPassportNumber() +
                " has been sent a link(email: " + newClient.getEmail() + ") to fill in the documents");
        return ResponseEntity.status(HttpStatus.CREATED).body(clientForAnswer);
    }


    @CrossOrigin
    @GetMapping("/getAllClients")
    ResponseEntity<?> selectClientBy(){
        List<ClientDTO> allClientsDTO = clientService.getAllClients();
        return ResponseEntity.status(HttpStatus.OK).body(allClientsDTO);
    }


    @CrossOrigin
    @PostMapping("/selectClient")
    ResponseEntity<?> selectClientBy(@RequestParam(name="clientID", required=false) String id,
                                     @RequestParam(name="clientPassportNumber", required=false) String passportNumber,
                                     @RequestParam(name="clientTelephon", required=false) String telephon,
                                     @RequestParam(name="clientEmail", required=false) String email,
                                     @RequestParam(name="clientSurName", required=false) String surName) {

        List<ClientDTO> foundClients = clientService.searchClients(id, passportNumber, telephon, email, surName);
        return ResponseEntity.status(HttpStatus.OK).body(foundClients);
    }



    @CrossOrigin
    @PatchMapping("/modifyClientData")
    ResponseEntity<?> modifyClientData(@RequestParam(name="clientPassportNumber", required=true) String clientPassportNumber,
                                       @RequestParam(name="clientKXNumber", required=false) String kxNumber,
                                       @RequestParam(name="clientFirstName", required=false) String clientFirstName,
                                       @RequestParam(name="clientLastName", required=false) String clientLastName,
                                       @RequestParam(name="clientSurname", required=false) String clientSurname,
                                       @RequestParam(name="clientEmail", required=false) String clientEmail,
                                       @RequestParam(name="clientTelefon", required=false) String clientTelefon,
                                       @RequestParam(name="clientServiceDescription", required=false) String clientServiceDescription,
                                       @RequestParam(name="clientFamilyID", required=false) String clientFamilyID,
                                       @RequestParam(name="clientStartPaymentDate", required=false) String clientStartPaymentDateStr,
                                       @RequestParam(name="clientStartServiceDate", required=false) String clientStartServiceDateStr,
                                       @RequestParam(name="clientBlocked", required=false) Boolean clientBlocked,
                                       @RequestParam(name="clientBlockedReasonDescription", required=false) String clientBlockedReasonDescription,
                                       @RequestParam(name="clientBlockDate", required=false) String clientBlockDateStr,
                                       @RequestParam(name="clientID_Doctor", required=false) String clientID_Doctor){
        //    @RequestParam(name="clientIDPaymentPlan", required=false) String clientIDPaymentPlan,


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime clientStartPaymentDate = null;
        LocalDateTime clientStartServiceDate = null;
        LocalDateTime clientBlockDate = null;

        if(!clientStartPaymentDateStr.isEmpty()){
            clientStartPaymentDate = LocalDateTime.parse(clientStartPaymentDateStr, formatter);
        }
        if(!clientStartServiceDateStr.isEmpty()){
            clientStartServiceDate = LocalDateTime.parse(clientStartServiceDateStr, formatter);
        }
        if(!clientBlockDateStr.isEmpty()){
            clientBlockDate = LocalDateTime.parse(clientBlockDateStr, formatter);
        }

        Client clientAfterRedact = clientService.redactClientData(clientPassportNumber, kxNumber, clientFirstName, clientLastName, clientSurname,
                clientEmail, clientTelefon, clientServiceDescription, clientFamilyID, clientStartPaymentDate,
                clientStartServiceDate, clientBlocked, clientBlockedReasonDescription, clientBlockDate, clientID_Doctor);

        if(clientAfterRedact != null) {
            ClientDTO clientForAnswer = clientService.mapClient_toClientDTOclass(clientAfterRedact);
            return ResponseEntity.status(HttpStatus.CREATED).body(clientForAnswer);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such client with passport number = " + clientPassportNumber);
        }
    }


    @CrossOrigin
    @PostMapping("/createNewDoctor")
    ResponseEntity<?> setDoctor(@RequestBody DoctorDTO doctorDTO) {
        Doctor newDoctor = clientService.createNewDoctor(doctorDTO);
        DoctorDTO doctorDTO_forAnswer = clientService.mapDoctor_toDoctorDTO(newDoctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorDTO_forAnswer);
    }

    @CrossOrigin
    @GetMapping("/getDoctorType")
    ResponseEntity<?> getDoctorType() {
        List<String> doctorTypeList = clientService.getDoctorType();
        return ResponseEntity.status(HttpStatus.OK).body(doctorTypeList);
    }


    @CrossOrigin
    @GetMapping("/selectAllDoctors")
    ResponseEntity<?> selectDoctors() {
        List<DoctorDTO> doctorDTOList = clientService.findAllDoctors();
        return ResponseEntity.status(HttpStatus.OK).body(doctorDTOList);
    }

    @CrossOrigin
    @PatchMapping("/modifyDoctor")
    ResponseEntity<?> modifyDoctor(@RequestParam(name="doctorID", required=true) String doctorID,
                                   @RequestParam(name="doctorFirstName", required = false) String doctorFirstName,
                                   @RequestParam(name="doctorLastName", required = false) String doctorLastName,
                                   @RequestParam(name="doctorSureName", required = false) String doctorSureName,
                                   @RequestParam(name="doctorTelefon", required = false) String doctorTelefon,
                                   @RequestParam(name="doctorEmail", required = false) String doctorEmail,
                                   @RequestParam(name="doctorAddres", required = false) String doctorAddres,
                                   @RequestParam(name="description", required = false) String description,
                                   @RequestParam(name="doctorType", required = false) String doctorType){

        Doctor modifiedDoctor = clientService.redactDoctor(doctorID, doctorFirstName, doctorLastName,doctorSureName,
                doctorTelefon, doctorEmail, doctorAddres, description,
                doctorType);

        if(modifiedDoctor != null){
            DoctorDTO doctorDTO_forAnswer = clientService.mapDoctor_toDoctorDTO(modifiedDoctor);
            return ResponseEntity.status(HttpStatus.CREATED).body(doctorDTO_forAnswer);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such doctor with ID = " + doctorID);
        }
    }


    @CrossOrigin
    @PostMapping("/createNewAssignment")
    ResponseEntity<?> createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        Marker marker = clientService.getLogMarker();
        String doctorID = assignmentDTO.getDoctorId();
        String clientID = assignmentDTO.getClientId();
        if(doctorID == null || clientID == null){
            LOGGER.error(marker, "doctorID= " + doctorID + " / clientID= " + clientID);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("It is not possible to create a new Assignment because clientID or doctorID == null");
        }


        Assignment newAssignment = clientService.createNewAssignment(assignmentDTO);
        AssignmentDTO assignmentDTO_forAnswer = clientService.mapAssignment_toAssignmentDTO(newAssignment);
        if(assignmentDTO_forAnswer != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(assignmentDTO_forAnswer);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Creation error new Assignment");
        }
    }

    @CrossOrigin
    @GetMapping("/getAssignmentType")
    ResponseEntity<?> getAssignmentType() {
        List<String> assignmentTypeList = clientService.getAssignmentType();
        return ResponseEntity.status(HttpStatus.OK).body(assignmentTypeList);
    }

    @CrossOrigin
    @GetMapping("/selectAllAssignments")
    ResponseEntity<?> selectAssignments() {
        List<AssignmentDTO> assignmentDTOList = clientService.getAllAssignments();
        return ResponseEntity.status(HttpStatus.OK).body(assignmentDTOList);
    }

    @CrossOrigin
    @GetMapping("/getUnfulfilledAssignments")
    ResponseEntity<?> getUnfulfilledAssignments(){
        List<AssignmentDTO> assignmentDTOList = clientService.getUnfulfilledAssignments();
        return ResponseEntity.status(HttpStatus.OK).body(assignmentDTOList);
    }


    @CrossOrigin
    @PatchMapping("/modifyAssignment")
    ResponseEntity<?> modifyAssignment(@RequestParam(name="assignmentID", required=true) String assignmentID,
                                   @RequestParam(name="assignmentCheckupAddress", required = false) String assignmentCheckupAddress,
                                   @RequestParam(name="assignmentCheckupMobile", required = false) String assignmentCheckupMobile,
                                   @RequestParam(name="assignmentCheckupEmail", required = false) String assignmentCheckupEmail,
                                   @RequestParam(name="assignmentCheckupDescription", required = false) String assignmentCheckupDescription,
                                   @RequestParam(name="assignmentDateTimeWhenToDo", required = false) String assignmentDateTimeWhenToDo,
                                   @RequestParam(name="assignmentDescription", required = false) String assignmentDescription,
                                   @RequestParam(name="assignmentClientId", required = false) String assignmentClientId,
                                   @RequestParam(name="assignmentDoctorId", required = false) String assignmentDoctorId,
                                   @RequestParam(name="assignmentType", required = false) String assignmentType,
                                   @RequestParam(name="assignmentIsDone", required = false) String assignmentIsDone){

        Assignment modifiedAssignment = clientService.redactAssignment(assignmentID, assignmentCheckupAddress, assignmentCheckupMobile,
                                        assignmentCheckupEmail, assignmentCheckupDescription, assignmentDateTimeWhenToDo,
                                        assignmentDescription, assignmentClientId, assignmentDoctorId, assignmentType, assignmentIsDone);

        if(modifiedAssignment != null){
            AssignmentDTO assignmentDTO_forAnswer = clientService.mapAssignment_toAssignmentDTO(modifiedAssignment);
            return ResponseEntity.status(HttpStatus.CREATED).body(assignmentDTO_forAnswer);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such assignment with ID = " + assignmentID);
        }
    }


    @CrossOrigin
    @PostMapping("/getAssignmentsForDay")
    ResponseEntity<?> getAssignmentsForDay(@RequestParam(name="assignmentDateTimeWhenToDo", required=true) String assignmentDateTimeWhenToDo){
        List<AssignmentDTO> assignmentForDay = clientService.getAssignmentsForDay(assignmentDateTimeWhenToDo);
        return ResponseEntity.status(HttpStatus.OK).body(assignmentForDay);
    }

//    @Autowired
//    HttpServletRequest httpServletRequest;



}

