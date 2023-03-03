package com.example.newmedicalservice.repository;

import com.example.newmedicalservice.dto.ImageTemplatesClientDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImageTemplatesRepositories extends JpaRepository<ImageTemplatesClientDocuments, Long> {
    ImageTemplatesClientDocuments getReferenceById(Long id);
}
