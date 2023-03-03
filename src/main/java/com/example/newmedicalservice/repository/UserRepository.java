package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.MedicalServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<MedicalServiceUser, Integer> {
    MedicalServiceUser findByLogin(String login);
}
