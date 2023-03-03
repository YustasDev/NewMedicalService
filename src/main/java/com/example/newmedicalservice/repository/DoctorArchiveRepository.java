package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.DoctorArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DoctorArchiveRepository extends JpaRepository<DoctorArchive, Long> {
    Optional<DoctorArchive> findByIdClient(String idClient);
    Optional<DoctorArchive> findByIdDoctor(Integer idDoctor);

}
