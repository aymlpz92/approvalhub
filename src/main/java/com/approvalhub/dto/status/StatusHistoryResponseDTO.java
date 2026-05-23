package com.approvalhub.dto.status;

import com.approvalhub.domain.enums.Status;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class StatusHistoryResponseDTO {
    private Long id;
    private String documentTitle;
    private String changeByUsername;
    private Status oldStatus;
    private Status newStatus;
    private Instant changedAt;
}
