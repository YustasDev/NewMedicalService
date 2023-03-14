package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.Assignment;
import com.example.newmedicalservice.dto.ClientDocs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findById(Long id);

    List<Assignment> findByDateTimeWhenToDoBetween(LocalDateTime dataTime, LocalDateTime dataTimePlusDay);
}
