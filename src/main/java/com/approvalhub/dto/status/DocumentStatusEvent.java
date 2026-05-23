package com.approvalhub.dto.status;


import com.approvalhub.domain.entity.Document;
import com.approvalhub.domain.entity.User;
import com.approvalhub.domain.enums.Status;
import lombok.Data;

@Data
public class DocumentStatusEvent {
    private Status oldStatus;
    private Status newStatus;
    private String comment;
    private Long documentId;
    private Long changedByUserId;

}