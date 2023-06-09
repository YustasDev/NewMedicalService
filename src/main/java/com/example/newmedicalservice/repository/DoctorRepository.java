package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Optional<Doctor> findById(Integer id);
    List<Doctor> findAll();


}
