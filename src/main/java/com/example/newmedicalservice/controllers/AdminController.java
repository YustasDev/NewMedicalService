package com.example.newmedicalservice.controllers;


import com.example.newmedicalservice.dto.MedicalServiceUser;
import com.example.newmedicalservice.dtoForAnswers.ClientDTO;
import com.example.newmedicalservice.dtoForAnswers.MedicalServiceUserDTO;
import com.example.newmedicalservice.service.AdminService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @CrossOrigin
    @GetMapping("/getUserRole")
    ResponseEntity<?> getUserRole() {
        List<String> userRoleList = adminService.getUserRoles();
        return ResponseEntity.status(HttpStatus.OK).body(userRoleList);
    }


    @CrossOrigin
    @PostMapping("/createNewUser")
    ResponseEntity<?> setUser(@RequestBody MedicalServiceUserDTO medicalServiceUserDTO) {

        MedicalServiceUser newUser = adminService.createNewUser(medicalServiceUserDTO);
        if(newUser != null){
            MedicalServiceUserDTO medicalServiceUserDTO_forAnswer =
                    adminService.mapMedicalServiceUser_toMedicalServiceUserDTO(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(medicalServiceUserDTO_forAnswer);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when creating a new user");
        }
    }

    @CrossOrigin
    @GetMapping("/getAllUsers")
    ResponseEntity<?> selectAllUsers(){
        List<MedicalServiceUserDTO> allUsersDTO = adminService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsersDTO);
    }

    @CrossOrigin
    @PatchMapping("/modifyUser")
    ResponseEntity<?> modifyClientData(@RequestParam(name="userId", required=true) String userId,
                                       @RequestParam(name="userName", required=false) String userName,
                                       @RequestParam(name="userEmail", required=false) String userEmail,
                                       @RequestParam(name="userTel", required=false) String userTel){

        MedicalServiceUser userAfterRedact = adminService.redactUserData(userId, userName, userEmail, userTel);
        if(userAfterRedact != null) {
            MedicalServiceUserDTO medicalServiceUserDTO
                    = adminService.mapMedicalServiceUser_toMedicalServiceUserDTO(userAfterRedact);
            return ResponseEntity.status(HttpStatus.CREATED).body(medicalServiceUserDTO);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such user with ID = " + userId);
        }



    }




}
