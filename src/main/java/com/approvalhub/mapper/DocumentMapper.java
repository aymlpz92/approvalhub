package com.approvalhub.mapper;

import com.approvalhub.domain.entity.Document;
import com.approvalhub.dto.document.DocumentCreateDTO;
import com.approvalhub.dto.document.DocumentResponseDTO;
import com.approvalhub.dto.document.DocumentStatusUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public Document toDocumentEntity(DocumentCreateDTO documentCreateDTO) {
        Document document = new Document();
        document.setTitle(documentCreateDTO.getTitle());
        document.setDescription(documentCreateDTO.getDescription());
        return document;
    }

    public Document  toDocumentEntity(DocumentStatusUpdateDTO documentStatusUpdateDTO) {
        Document document = new Document();
        document.setStatus(documentStatusUpdateDTO.getStatus());
        document.setComment(documentStatusUpdateDTO.getComment());
        return  document;
    }

    public DocumentCreateDTO toDocumentCreateDTO(Document document) {
        DocumentCreateDTO documentCreateDTO = new DocumentCreateDTO();
        documentCreateDTO.setTitle(document.getTitle());
        documentCreateDTO.setDescription(document.getDescription());
        return documentCreateDTO;
    }

    public DocumentResponseDTO toDocumentResponseDTO(Document doc) {
        DocumentResponseDTO docResponseDTO = new DocumentResponseDTO();
        docResponseDTO.setId(doc.getId());
        docResponseDTO.setTitle(doc.getTitle());
        docResponseDTO.setDescription(doc.getDescription());
        docResponseDTO.setStatus(doc.getStatus());
        docResponseDTO.setCreatedAt(doc.getCreatedAt());
        docResponseDTO.setOwnerUsername(doc.getOwner().getUsername());
        return docResponseDTO;
    }

    public  DocumentStatusUpdateDTO toDocumentStatusUpdateDTO(Document document) {
        DocumentStatusUpdateDTO documentStatusUpdateDTO = new DocumentStatusUpdateDTO();
        documentStatusUpdateDTO.setStatus(document.getStatus());
        documentStatusUpdateDTO.setComment(document.getComment());
        return documentStatusUpdateDTO;
    }
}
