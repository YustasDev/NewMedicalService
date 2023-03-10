package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    List<Client> findAll();
    Optional<Client> findById(String id);
    Optional<Client> findByPassportNumber(String passportNumber);
    List<Client> findByTelephone (String telephone);
    List<Client> findByEmail(String email);

    //@Query("SELECT c FROM Client c WHERE c.surname = :surname")
    List<Client> findBySurName(String surName);
}
