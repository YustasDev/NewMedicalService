package com.example.newmedicalservice.controllers;


import com.example.newmedicalservice.dto.*;
import com.example.newmedicalservice.dtoForAnswers.ClientDTO;
import com.example.newmedicalservice.dtoForAnswers.ClientDocsDTO;
import com.example.newmedicalservice.dtoForAnswers.DoctorDTO;
import com.example.newmedicalservice.dtoForAnswers.FamilyDTO;
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
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/customers")
public class ClientController {

    private static final Logger LOGGER = LogManager.getLogger(ClientController.class);
    private static final Marker USER1 = MarkerManager.getMarker("USER1");
    private static final Marker USER2 = MarkerManager.getMarker("USER2");
    private static final Marker ADMIN = MarkerManager.getMarker("ADMIN");
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

    @CrossOrigin
    @GetMapping("/getFamily")
    ResponseEntity<?> getFamily(){
        List<FamilyDTO> familyDTOList = clientService.getFamilyList();
        return ResponseEntity.status(HttpStatus.OK).body(familyDTOList);
    }

    @CrossOrigin
    @PatchMapping("/setFamilyDate")
    ResponseEntity<?> setFamilyDate(@RequestBody FamilyDTO familyDTO) {   // todo FRONT ==> clientList are always null
        Family familyForModify = null;
        Optional<Family> familyForModifyOpt = familyRepository.findById(familyDTO.getId());
        if(familyForModifyOpt.isPresent()) {
            familyForModify = familyForModifyOpt.get();
            if (familyForModify != null) {
                familyForModify.setFamilyName(familyDTO.getFamilyName());
                familyForModify.setFamilyHead(familyDTO.getFamilyHead());
                familyForModify.setFamilyMobile(familyDTO.getFamilyMobile());
                familyForModify.setDescription(familyDTO.getDescription());
                familyRepository.save(familyForModify);
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no family with this ID: " + familyDTO.getId());
        }
        FamilyDTO familyDTOforAnswer = clientService.mapFamily_toFamilyDTO(familyForModify);
        return ResponseEntity.status(HttpStatus.CREATED).body(familyDTOforAnswer);
    }


    @CrossOrigin
    @PostMapping("/createNewClient")
    ResponseEntity<?> setClient(@RequestBody ClientDTO clientDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName_inService = authentication.getName();
        Marker marker = null;
        if (currentUserName_inService.equals("user1")){
            marker = USER1;
        }
        else if(currentUserName_inService.equals("user2")){
            marker = USER2;
        }
        else if (currentUserName_inService.equals("admin")){
            marker = ADMIN;
        }
        else {
            log.error("user logged in is not defined"); // todo can't be
        }


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
            family = new Family();   // TODO  Check IT!
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
    @GetMapping("/selectClient")
    ResponseEntity<?> selectClientBy(@RequestParam(name="clientID", required=false) String id,
                                     @RequestParam(name="clientPassportNumber", required=false) String passportNumber,
                                     @RequestParam(name="clientTelephon", required=false) String telephon,
                                     @RequestParam(name="clientEmail", required=false) String email,
                                     @RequestParam(name="clientSurName", required=false) String surName) {

        List<ClientDTO> foundClients = clientService.searchClients(id, passportNumber, telephon, email, surName);
        return ResponseEntity.status(HttpStatus.OK).body(foundClients);
    }

    @CrossOrigin
    @GetMapping("/returnCustomizedPDFTemplate")
    ResponseEntity<?> returnImageTemplate(@RequestParam(name="clientID", required=true) String clientID,
                                          @RequestParam(name="clientContract", required=false) String clientContract,
                                          @RequestParam(name="clientAgreement", required=false) String clientAgreement,
                                          @RequestParam(name="clientQuestionnaire", required=false) String clientQuestionnaire) {

        byte[] pdfTemplate = null;
        try {
            if(clientContract != null) {
                pdfTemplate = clientService.restoreContract_fromDB(clientID);
            }
//            else if (clientAgreement != null){
//                clientService.restoreAgreement_fromDB(clientID);
//            }
//            else if (clientQuestionnaire != null){
//                clientService.restoreQuestionnaire_fromDB(clientID);
//            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("The error has occurred ==> " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("PDF document template not received");
        }
        return ResponseEntity.status(HttpStatus.OK).body(pdfTemplate);

    }


    @CrossOrigin
    @PatchMapping("/setUpDocsForClient")
    ResponseEntity<?> setDocs(@RequestParam(name="clientID", required=true) String clientID,
                              @RequestParam(name="clientFoto", required=false) MultipartFile clientFoto,
                              @RequestParam(name="clientContract", required=false) MultipartFile contract,
                              @RequestParam(name="clientAgreement", required=false) MultipartFile agreement,
                              @RequestParam(name="clientQuestionnaire", required=false) MultipartFile questionnaire) {

        ClientDocsDTO clientDocsDTO = null;
        try {
            clientDocsDTO = clientService.setUpClientData(clientID, clientFoto,
                    contract, agreement, questionnaire);
        } catch (IOException ioe) {
            log.error("Image file read error ==> " + ioe.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file read error ==> " + ioe.getMessage());
        }
        log.info("Documents added to the ClientDocs repository: " + clientDocsDTO.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(clientDocsDTO);
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
                                       @RequestParam(name="clientStartPaymentDate", required=false) LocalDateTime clientStartPaymentDate,
                                       @RequestParam(name="clientStartServiceDate", required=false) LocalDateTime clientStartServiceDate,
                                       @RequestParam(name="clientBlocked", required=false) Boolean clientBlocked,
                                       @RequestParam(name="clientBlockedReasonDescription", required=false) String clientBlockedReasonDescription,
                                       @RequestParam(name="clientBlockDate", required=false) LocalDateTime clientBlockDate,
                                       @RequestParam(name="clientID_Doctor", required=false) String clientID_Doctor){
        //    @RequestParam(name="clientIDPaymentPlan", required=false) String clientIDPaymentPlan,


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







}

