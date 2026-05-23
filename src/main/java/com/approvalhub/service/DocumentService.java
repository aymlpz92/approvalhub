package com.approvalhub.service;

import com.approvalhub.domain.entity.Document;
import com.approvalhub.domain.entity.User;
import com.approvalhub.dto.document.DocumentCreateDTO;
import com.approvalhub.dto.document.DocumentResponseDTO;
import com.approvalhub.dto.document.DocumentStatusUpdateDTO;
import com.approvalhub.dto.status.DocumentStatusEvent;
import com.approvalhub.kafka.KafkaEventProducer;
import com.approvalhub.mapper.DocumentMapper;
import com.approvalhub.repository.DocumentRepository;
import com.approvalhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.approvalhub.domain.enums.Role.REVIEWER;
import static com.approvalhub.domain.enums.Role.SUBMITTER;
import static com.approvalhub.domain.enums.Status.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final UserRepository userRepository;
    private final KafkaEventProducer kafkaEventProducer;


    public DocumentCreateDTO createDocument(DocumentCreateDTO documentCreateDTO) throws  Exception {
        Document document = documentMapper.toDocumentEntity(documentCreateDTO);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Username from token: " + username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (user.getRole().equals(SUBMITTER)) {
            document.setStatus(DRAFT);
            document.setOwner(user);
            documentRepository.save(document);
            return documentMapper.toDocumentCreateDTO(document);
        }
        throw new RuntimeException("Not allowed to create document");
    }

    public List<DocumentResponseDTO> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(documentMapper::toDocumentResponseDTO)
                .toList();
    }

    public DocumentResponseDTO getDocumentById(Long id) {
        return documentMapper.toDocumentResponseDTO(documentRepository.findById(id).get());
    }

    public DocumentResponseDTO getDocumentByTitle(String title) {
        return documentMapper.toDocumentResponseDTO(documentRepository.getDocumentByTitle(title));
    }

    public DocumentStatusUpdateDTO submitDocument(Long documentId, String comment) throws RuntimeException {
        Document document = documentRepository.findById(documentId).get();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (user.getRole().equals(SUBMITTER)) {
            if (document.getStatus() == DRAFT) {
                document.setStatus(PENDING);
                document.setComment(comment);
                documentRepository.save(document);
                DocumentStatusEvent documentStatusEvent = new DocumentStatusEvent();
                documentStatusEvent.setOldStatus(DRAFT);
                documentStatusEvent.setNewStatus(PENDING);
                documentStatusEvent.setDocumentId(document.getId());
                documentStatusEvent.setChangedByUserId(user.getId());
                documentStatusEvent.setComment(comment);
                kafkaEventProducer.sendEvent(documentStatusEvent);
                return documentMapper.toDocumentStatusUpdateDTO(document);
            }
        }

        throw  new RuntimeException("Permission refusée");

    }

    public DocumentStatusUpdateDTO approveDocument(Long  documentId, String comment) throws RuntimeException {
        Document document = documentRepository.findById(documentId).get();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (user.getRole().equals(REVIEWER)) {
            if (document.getStatus() == PENDING) {
                document.setStatus(APPROVED);
                document.setComment(comment);
                documentRepository.save(document);
                DocumentStatusEvent documentStatusEvent = new DocumentStatusEvent();
                documentStatusEvent.setOldStatus(PENDING);
                documentStatusEvent.setNewStatus(APPROVED);
                documentStatusEvent.setDocumentId(document.getId());
                documentStatusEvent.setChangedByUserId(user.getId());
                documentStatusEvent.setComment(comment);
                kafkaEventProducer.sendEvent(documentStatusEvent);
                return documentMapper.toDocumentStatusUpdateDTO(document);
            }

        }

        throw  new RuntimeException("Permission refusée");
    }

    public DocumentStatusUpdateDTO rejectDocument(Long documentId, String comment) throws RuntimeException {
        Document document = documentRepository.findById(documentId).get();
        String  username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (user.getRole().equals(REVIEWER)) {
            if (document.getStatus() == PENDING) {
                document.setStatus(REJECTED);
                document.setComment(comment);
                documentRepository.save(document);
                DocumentStatusEvent documentStatusEvent = new DocumentStatusEvent();
                documentStatusEvent.setOldStatus(PENDING);
                documentStatusEvent.setNewStatus(REJECTED);
                documentStatusEvent.setDocumentId(document.getId());
                documentStatusEvent.setChangedByUserId(user.getId());
                documentStatusEvent.setComment(comment);
                kafkaEventProducer.sendEvent(documentStatusEvent);
                return documentMapper.toDocumentStatusUpdateDTO(document);
            }
        }
        throw  new RuntimeException("Permission refusée");
    }




}
