package com.example.newmedicalservice.controllers;


import com.example.newmedicalservice.dto.Client;
import com.example.newmedicalservice.dto.ClientDocs;
import com.example.newmedicalservice.dto.StatusDocAnswer;
import com.example.newmedicalservice.dtoForAnswers.AssignmentDTO;
import com.example.newmedicalservice.dtoForAnswers.ClientDocsDTO;
import com.example.newmedicalservice.dtoForAnswers.QuestionnaireDTO;
import com.example.newmedicalservice.repository.ClientDocsRepository;
import com.example.newmedicalservice.repository.ClientRepository;
import com.example.newmedicalservice.repository.FamilyRepository;
import com.example.newmedicalservice.service.ClientService;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
@RequestMapping("/documents")
public class DisplayDocumentsTemplates {

    private static final Logger LOGGER = LogManager.getLogger(DisplayDocumentsTemplates.class);
    private ClientService clientService;
    private ClientRepository clientRepository;
    private ClientDocsRepository clientDocsRepository;

    @Autowired
    public DisplayDocumentsTemplates(ClientService clientService, ClientRepository clientRepository, ClientDocsRepository clientDocsRepository) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.clientDocsRepository = clientDocsRepository;
    }


    @CrossOrigin
    @PostMapping("/getDocumentStatus")
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
    @PostMapping("/getDocument")
    ResponseEntity<?> returnDocument(@RequestParam(name="clientID", required=true) String id,
                                     @RequestParam(name="documentType", required=true) String document) {

        byte[] pdfForSignature = null;
        try {
            if(document.equals("Contract")) {
                pdfForSignature = clientService.getCustomizedContract(id);
            }
            else if (document.equals("Agreement")){
                pdfForSignature = clientService.getCustomizedAgreement(id);
            }
//            else if (clientQuestionnaire != null){
//                clientService.restoreQuestionnaire_fromDB(clientID);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("The error has occurred ==> " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("PDF document template not received");
        }
        return ResponseEntity.status(HttpStatus.OK).body(pdfForSignature);
    }



    @CrossOrigin
    @PatchMapping("/setQuestionnaire")
    ResponseEntity<?> setQuestionnaire(@RequestBody QuestionnaireDTO questionnaireDTO) {
        Optional<Client> clientOptional = clientRepository.findById(questionnaireDTO.getClientID());
        if (clientOptional.isPresent()){
            try {
                Client client = clientOptional.get();
                ClientDocs clientDocsCurrentClient = client.getClientDocs();

                Gson ques = new Gson();
                String jsonObj = ques.toJson(questionnaireDTO);

                clientDocsCurrentClient.setQuestionnaire(jsonObj);
                client.setClientDocs(clientDocsCurrentClient);
                clientRepository.save(client);
            }
            catch (Exception e){
                LOGGER.error("Error when saving the questionnaire for the client: " + questionnaireDTO.getClientID());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error when saving the questionnaire for the client: " + questionnaireDTO.getClientID());
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client with id= '" + questionnaireDTO.getClientID() + "' does not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }


    @CrossOrigin
    @PatchMapping("/setClientFoto")
    ResponseEntity<?> setClientFoto(@RequestParam(name="clientID", required=true) String id,
                                    @RequestParam(name="foto", required=true) MultipartFile foto) {
        try {
            Boolean result = clientService.setClientFoto(id, foto);
            if(result){
                return ResponseEntity.status(HttpStatus.OK).body("Success");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed");
            }
        } catch (Exception ioe) {
            LOGGER.error("Image file read error ==> " + ioe.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file read error ==> " + ioe.getMessage());
        }
    }



    @CrossOrigin
    @PatchMapping(value = "/updateClientDocs")
    ResponseEntity<?> updateDocs(@RequestParam(name="clientID", required=true) String clientID,
                                 @RequestParam(name="docType", required = true) String docType,
                                 @RequestParam(name="signature", required=false) MultipartFile signature) {

        ClientDocsDTO clientDocsDTO = null;
        try {
            Boolean result = clientService.updateClientDocs(clientID, docType, signature);
            if(result){
                return ResponseEntity.status(HttpStatus.OK).body("Success");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed");
            }
        } catch (Exception ioe) {
            LOGGER.error("Image file read error ==> " + ioe.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file read error ==> " + ioe.getMessage());
        }
    }









 /*

    @GetMapping("/displayDocumentsTemplates/{id}")
    public String displayDocumentsTemplates(@PathVariable String id, Model model) {
        model.addAttribute("clientID", id);
        return "documentEntryScreen";
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

  */

//    @GetMapping("/")
//    public String getIndex(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserName_inService = authentication.getName();
//        Marker marker = null;
//        if (currentUserName_inService.equals("user1")){
//            marker = USER1;
//        }
//        else if(currentUserName_inService.equals("user2")){
//            marker = USER2;
//        }
//        else if (currentUserName_inService.equals("admin")){
//            marker = ADMIN;
//        }
//        else {
//            log.error("user logged in is not defined");
//        }
//        return "index";
//    }

//    @GetMapping("/")
//    public String getIndex() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserName_inService = authentication.getName();
//        return currentUserName_inService;
//    }
//



}
