package com.wu.euwallet.duplicatecheck.exception;

import com.wu.euwallet.duplicatecheck.constants.DuplicateCheckConstants;
import com.wu.euwallet.duplicatecheck.dto.DuplicateCheckResponse;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
@Log4j2
public class DuplicateCheckExceptionHandler {

    @ExceptionHandler(WUServiceException.class)
    public ResponseEntity<DuplicateCheckResponse> handleWUServiceException(WUServiceException ex) {
        log.error("WUServiceException occurred: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(DuplicateCheckResponse.builder()
                        .status(DuplicateCheckConstants.DUPLICATE_CHECK_ERROR_CODE)
                        .correlationId(UUID.randomUUID().toString())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DuplicateCheckResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.warn("Validation failed: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(DuplicateCheckResponse.builder()
                        .status(DuplicateCheckConstants.DUPLICATE_CHECK_ERROR_CODE)
                        .correlationId(UUID.randomUUID().toString())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DuplicateCheckResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(DuplicateCheckResponse.builder()
                        .status(DuplicateCheckConstants.DUPLICATE_CHECK_ERROR_CODE)
                        .correlationId(UUID.randomUUID().toString())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DuplicateCheckResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DuplicateCheckResponse.builder()
                        .status(DuplicateCheckConstants.DUPLICATE_CHECK_ERROR_CODE)
                        .correlationId(UUID.randomUUID().toString())
                        .message("Unexpected error occurred")
                        .build());
    }
}
