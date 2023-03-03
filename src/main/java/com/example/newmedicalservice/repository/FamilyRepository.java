package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FamilyRepository extends JpaRepository<Family, Integer> {

    List<Family> findAll();
    Optional<Family> findById(Integer id);

}
