package com.approvalhub.service;

import com.approvalhub.domain.entity.Document;
import com.approvalhub.domain.entity.User;
import com.approvalhub.domain.enums.Role;
import com.approvalhub.domain.enums.Status;
import com.approvalhub.dto.document.DocumentCreateDTO;
import com.approvalhub.dto.document.DocumentResponseDTO;
import com.approvalhub.dto.document.DocumentStatusUpdateDTO;
import com.approvalhub.dto.status.DocumentStatusEvent;
import com.approvalhub.exception.ResourceNotFoundException;
import com.approvalhub.kafka.KafkaEventProducer;
import com.approvalhub.mapper.DocumentMapper;
import com.approvalhub.repository.DocumentRepository;
import com.approvalhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
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
        return documentMapper.toDocumentResponseDTO(documentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document not found")));
    }

    public DocumentResponseDTO getDocumentByTitle(String title) {
        return documentMapper.toDocumentResponseDTO(documentRepository.getDocumentByTitle(title));
    }

    public DocumentStatusUpdateDTO submitDocument(Long documentId, String comment) throws RuntimeException {
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (checkRole(comment, document, user, SUBMITTER, DRAFT, PENDING)) {
            if (document.getOwner().equals(user)) {
                return documentMapper.toDocumentStatusUpdateDTO(document);
            }
        }
        throw  new RuntimeException("Permission denied");

    }

    public DocumentStatusUpdateDTO approveDocument(Long  documentId, String comment) throws RuntimeException {
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (checkRole(comment, document, user, REVIEWER, PENDING, APPROVED)) {
            if (document.getStatus() == PENDING) {
                documentEvent(comment, document, user, PENDING, APPROVED);
                return documentMapper.toDocumentStatusUpdateDTO(document);
            }
        }
        throw  new RuntimeException("Permission denied");
    }

    public DocumentStatusUpdateDTO rejectDocument(Long documentId, String comment) throws RuntimeException {
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        String  username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user =  userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (checkRole(comment, document, user, REVIEWER, PENDING, REJECTED))
            return documentMapper.toDocumentStatusUpdateDTO(document);

        throw  new RuntimeException("Permission denied");
    }

    private boolean checkRole(String comment, Document document, User user, Role role, Status oldStatus, Status newStatus) {
        if (user.getRole().equals(role)) {
            if (document.getStatus() == oldStatus) {
                documentEvent(comment, document, user, oldStatus, newStatus);
                return true;
            }
        }
        return false;
    }

    private void documentEvent(String comment, Document document, User user, Status oldStatus, Status newStatus) {
        document.setStatus(newStatus);
        document.setComment(comment);
        documentRepository.save(document);
        DocumentStatusEvent documentStatusEvent = new DocumentStatusEvent();
        documentStatusEvent.setOldStatus(oldStatus);
        documentStatusEvent.setNewStatus(newStatus);
        documentStatusEvent.setDocumentId(document.getId());
        documentStatusEvent.setChangedByUserId(user.getId());
        documentStatusEvent.setComment(comment);
        kafkaEventProducer.sendEvent(documentStatusEvent);
    }


}
