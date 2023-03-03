package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.ClientDocs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientDocsRepository extends JpaRepository<ClientDocs, Long> {

    Optional<ClientDocs> findById(Long id);
    Optional<ClientDocs> findByPassportNumber(String passportNumber);

}
