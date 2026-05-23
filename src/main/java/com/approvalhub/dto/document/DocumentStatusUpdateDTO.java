package com.approvalhub.dto.document;

import com.approvalhub.domain.enums.Status;
import lombok.Data;

@Data
public class DocumentStatusUpdateDTO {
    private Status status;
    private String comment;
}
