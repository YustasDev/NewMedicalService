package com.example.newmedicalservice.controllers;


import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Log4j2
@Controller
public class DisplayDocumentsTemplates {

    private static final Logger LOGGER = LogManager.getLogger(ClientController.class);
    private static final Marker USER1 = MarkerManager.getMarker("USER1");
    private static final Marker USER2 = MarkerManager.getMarker("USER2");
    private static final Marker ADMIN = MarkerManager.getMarker("ADMIN");


    @GetMapping("/displayDocumentsTemplates/{id}")
    public String displayDocumentsTemplates(@PathVariable String id, Model model) {
        model.addAttribute("clientID", id);
        return "documentEntryScreen";
    }

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
