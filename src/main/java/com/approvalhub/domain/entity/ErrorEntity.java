package com.approvalhub.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorEntity {
    private LocalDateTime timestamp;
    private String message;
    private int httpStatusCode;
}
