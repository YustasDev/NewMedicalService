package com.example.newmedicalservice.service;

import com.example.newmedicalservice.dto.MedicalServiceUser;
import com.example.newmedicalservice.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        MedicalServiceUser currentUser= userRepository.findByLogin(userName);
        if (currentUser == null) {
            log.error("Attempting to log in as ==> " + userName);
            throw new UsernameNotFoundException("Unknown user: " + userName);
        }
        UserDetails user = User.builder()
                .username(currentUser.getLogin())
                .password(currentUser.getPassword())
                .roles(currentUser.getUserRole().name())
                .build();
        log.info("log in as ==> " + userName);
        return user;
    }


}
