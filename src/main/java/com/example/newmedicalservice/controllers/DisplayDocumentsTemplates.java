package com.example.newmedicalservice.controllers;


import com.example.newmedicalservice.dtoForAnswers.ClientDocsDTO;
import com.example.newmedicalservice.service.ClientService;
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

@Log4j2
@Controller
public class DisplayDocumentsTemplates {

    private static final Logger LOGGER = LogManager.getLogger(DisplayDocumentsTemplates.class);
    private ClientService clientService;

    @Autowired
    public DisplayDocumentsTemplates(ClientService clientService) {
        this.clientService = clientService;
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
