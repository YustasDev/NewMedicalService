package com.example.newmedicalservice.service;


import com.example.newmedicalservice.dto.Client;
import com.example.newmedicalservice.dto.Doctor;
import com.example.newmedicalservice.dto.MedicalServiceUser;
import com.example.newmedicalservice.dtoForAnswers.ClientDTO;
import com.example.newmedicalservice.dtoForAnswers.DoctorDTO;
import com.example.newmedicalservice.dtoForAnswers.MedicalServiceUserDTO;
import com.example.newmedicalservice.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class AdminService {

    private UserRepository userRepository;
    private ClientService clientService;
    private static final Logger LOGGER = LogManager.getLogger(AdminService.class);

    @Autowired
    public AdminService(UserRepository userRepository, ClientService clientService) {
        this.userRepository = userRepository;
        this.clientService = clientService;
    }


    public MedicalServiceUser createNewUser(MedicalServiceUserDTO medicalServiceUserDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName_inService = authentication.getName();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        MedicalServiceUser newUser = new MedicalServiceUser();
        Marker marker = clientService.getLogMarker();

        try {
            newUser.setLogin(medicalServiceUserDTO.getLogin());
            newUser.setPassword(passwordEncoder.encode(medicalServiceUserDTO.getPassword()));
            newUser.setUserName(medicalServiceUserDTO.getUserName());
            newUser.setUserEmail(medicalServiceUserDTO.getUserEmail());
            newUser.setUserTel(medicalServiceUserDTO.getUserTel());
            newUser.setUserRole(MedicalServiceUser.UserRole.valueOf(medicalServiceUserDTO.getUserRole()));
            userRepository.save(newUser);
            LOGGER.info(marker, "There is currently a user in the service: '" + currentUserName_inService + "'");
            LOGGER.info(marker, "The new user has been created: " + newUser.toString());
        }
        catch (Exception e){
            LOGGER.error(marker, "There is currently a user in the service: '" + currentUserName_inService + "'");
            LOGGER.error(marker, "Error when creating a new user: " + newUser.toString() + " [ with Error ==> " + e + "]");
            return null;
        }
        return newUser;
    }

    public MedicalServiceUserDTO mapMedicalServiceUser_toMedicalServiceUserDTO(MedicalServiceUser medicalServiceUser){
        MedicalServiceUserDTO medicalServiceUserDTO = new MedicalServiceUserDTO();

        medicalServiceUserDTO.setId(String.valueOf(medicalServiceUser.getId()));
        medicalServiceUserDTO.setLogin(medicalServiceUser.getLogin());
        medicalServiceUserDTO.setPassword(medicalServiceUser.getPassword());
        medicalServiceUserDTO.setUserName(medicalServiceUser.getUserName());
        medicalServiceUserDTO.setUserTel(medicalServiceUser.getUserTel());
        medicalServiceUserDTO.setUserEmail(medicalServiceUser.getUserEmail());
        medicalServiceUserDTO.setUserRole(medicalServiceUser.getUserRole().name());
        return  medicalServiceUserDTO;
    }


    public List<MedicalServiceUserDTO> getAllUsers() {
        List<MedicalServiceUserDTO> allUsersDTO = new ArrayList<>();
        List<MedicalServiceUser> allUsers = userRepository.findAll();
        for(MedicalServiceUser user : allUsers){
            MedicalServiceUserDTO medicalServiceUserDTO =
                    mapMedicalServiceUser_toMedicalServiceUserDTO(user);
            allUsersDTO.add(medicalServiceUserDTO);
        }
        return allUsersDTO;
    }


    public MedicalServiceUser redactUserData(String userId, String userName, String userEmail, String userTel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName_inService = authentication.getName();
        Marker marker = clientService.getLogMarker();
        Optional<MedicalServiceUser> optionalUser = userRepository.findById(Integer.valueOf(userId));
        MedicalServiceUser userForModify = null;
        try {
            if (optionalUser.isPresent()) {
                userForModify = optionalUser.get();

                if (!userName.isEmpty()) {
                    userForModify.setUserName(userName);
                }
                if (!userEmail.isEmpty()) {
                    userForModify.setUserEmail(userEmail);
                }
                if (!userTel.isEmpty()) {
                    userForModify.setUserTel(userTel);
                }
                userRepository.save(userForModify);
                LOGGER.info(marker, "There is currently a user in the service: '" + currentUserName_inService + "'");
                LOGGER.info(marker, "The user with ID = " + userId + " has been edited");
            }
        }
        catch (Exception e){
            LOGGER.error(marker, "There is currently a user in the service: '" + currentUserName_inService + "'");
            LOGGER.error(marker, "Error during modification of the user with ID = " + userId + " [ with Error ==> " + e + "]");
            return null;
        }
        return userForModify;
    }


    public List<String> getUserRoles() {
        List<String> userRoles = new ArrayList<>();
        MedicalServiceUser.UserRole[] userRolesArray = MedicalServiceUser.UserRole.values();
        List<MedicalServiceUser.UserRole> listOfUserRoles = Arrays.asList(userRolesArray);
        for (MedicalServiceUser.UserRole mur : listOfUserRoles) {
            String role = mur.name();
            userRoles.add(role);
        }
        return userRoles;
    }
}
