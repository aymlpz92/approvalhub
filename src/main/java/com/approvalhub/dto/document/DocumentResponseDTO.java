package com.approvalhub.dto.document;

import com.approvalhub.domain.enums.Status;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class DocumentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private String ownerUsername;
    private Instant createdAt;
}
