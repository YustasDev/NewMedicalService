package com.example.newmedicalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NewMedicalServiceApplication {

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
//
//    }




}
