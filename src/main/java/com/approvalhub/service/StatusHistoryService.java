package com.approvalhub.service;

import com.approvalhub.domain.entity.Document;
import com.approvalhub.domain.entity.StatusHistory;
import com.approvalhub.domain.entity.StatusHistoryIndex;
import com.approvalhub.domain.entity.User;
import com.approvalhub.dto.status.DocumentStatusEvent;
import com.approvalhub.mapper.StatusHistoryMapper;
import com.approvalhub.repository.DocumentRepository;
import com.approvalhub.repository.StatusHistoryIndexRepository;
import com.approvalhub.repository.StatusHistoryRepository;
import com.approvalhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatusHistoryService {

    private final StatusHistoryIndexRepository statusHistoryIndexRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final StatusHistoryMapper statusHistoryMapper;

    @Transactional
    public void updateDocumentStatus(DocumentStatusEvent documentStatusEvent) {
        Document document = documentRepository.getReferenceById(documentStatusEvent.getDocumentId());
        User user = userRepository.getReferenceById(documentStatusEvent.getChangedByUserId());
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setOldStatus(documentStatusEvent.getOldStatus());
        statusHistory.setNewStatus(documentStatusEvent.getNewStatus());
        statusHistory.setComment(documentStatusEvent.getComment());
        statusHistory.setDocument(document);
        statusHistory.setChangedBy(user);
        statusHistoryRepository.save(statusHistory);
        newStatusHistoryIndex(statusHistory);
        statusHistoryMapper.toStatusHistoryResponseDTO(statusHistory);
    }

    public void newStatusHistoryIndex(StatusHistory statusHistory) {
        StatusHistoryIndex statusHistoryIndex = new StatusHistoryIndex();
        statusHistoryIndex.setId(statusHistory.getId().toString());
        statusHistoryIndex.setDocumentTitle(statusHistory.getDocument().getTitle());
        statusHistoryIndex.setOldStatus(statusHistory.getOldStatus().toString());
        statusHistoryIndex.setNewStatus(statusHistory.getNewStatus().toString());
        statusHistoryIndex.setChangeByUsername(statusHistory.getChangedBy().getUsername());
        statusHistoryIndex.setChangedAt(statusHistory.getChangeAt().toString());
        statusHistoryIndexRepository.save(statusHistoryIndex);
    }

}
