package com.approvalhub.repository;

import com.approvalhub.domain.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document getDocumentByTitle(String title);
}
