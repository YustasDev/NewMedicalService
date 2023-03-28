package com.example.newmedicalservice;

import com.example.newmedicalservice.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class NewMedicalServiceApplication {

    @Autowired
    private ClientService clientService;

    public static void main(String[] args) {
        SpringApplication.run(NewMedicalServiceApplication.class, args);
    }

//    @PostConstruct
//    public void fillDocumentTemplates() {
//        boolean resultFillImageDocumentTemplates = clientService.fillImageDocumentTemplates();
//        if(resultFillImageDocumentTemplates == true){
//            System.out.println("The docs template table is complete");
//        }
//        else {
//            System.err.println("Error when filling in the document template table");
//        }
//    }
//



}
