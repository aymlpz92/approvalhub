package com.approvalhub.mapper;

import com.approvalhub.domain.entity.StatusHistory;
import com.approvalhub.dto.status.DocumentStatusEvent;
import com.approvalhub.dto.status.StatusHistoryResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class StatusHistoryMapper {

    public StatusHistoryResponseDTO toStatusHistoryResponseDTO(StatusHistory statusHistory) {
        StatusHistoryResponseDTO  statusHistoryResponseDTO  = new StatusHistoryResponseDTO();
        statusHistoryResponseDTO.setId(statusHistory.getId());
        statusHistoryResponseDTO.setDocumentTitle(statusHistory.getDocument().getTitle());
        statusHistoryResponseDTO.setOldStatus(statusHistory.getOldStatus());
        statusHistoryResponseDTO.setNewStatus(statusHistory.getNewStatus());
        statusHistoryResponseDTO.setChangeByUsername(statusHistory.getChangedBy().getUsername());
        statusHistoryResponseDTO.setChangedAt(statusHistory.getChangeAt());

        return statusHistoryResponseDTO;
    }


}
