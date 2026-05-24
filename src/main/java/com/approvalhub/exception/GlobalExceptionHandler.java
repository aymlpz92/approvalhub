package com.approvalhub.exception;

import com.approvalhub.domain.entity.ErrorEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorEntity> resourceNotFoundHandler(ResourceNotFoundException exception) {
        ErrorEntity errorEntity = new ErrorEntity();
        errorEntity.setMessage(exception.getMessage());
        errorEntity.setHttpStatusCode(HttpStatus.NOT_FOUND.value());
        errorEntity.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorEntity, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    public ResponseEntity<ErrorEntity> usernameAlreadyExistsHandler(UsernameAlreadyExists exception) {
        ErrorEntity errorEntity = new ErrorEntity();
        errorEntity.setMessage(exception.getMessage());
        errorEntity.setHttpStatusCode(HttpStatus.CONFLICT.value());
        errorEntity.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorEntity, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorEntity> runtimeExceptionHandler(RuntimeException exception) {
        ErrorEntity errorEntity = new ErrorEntity();
        errorEntity.setMessage(exception.getMessage());
        errorEntity.setHttpStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorEntity.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorEntity, HttpStatus.UNAUTHORIZED);
    }



}
