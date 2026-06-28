package com.biglibon.sharedlibrary.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Ortak handler sayesinde servisler aynı hata formatını döner.

    @ExceptionHandler(LibraryNotFoundException.class)
    public ResponseEntity<?> handle(LibraryNotFoundException exception, HttpServletRequest request) {
        ExceptionDetails exceptionDetails = new ExceptionDetails(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LibraryDuplicateException.class)
    public ResponseEntity<?> handle(LibraryDuplicateException exception, HttpServletRequest request) {
        ExceptionDetails exceptionDetails = new ExceptionDetails(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<?> handle(BookNotFoundException exception, HttpServletRequest request) {
        ExceptionDetails exceptionDetails = new ExceptionDetails(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookDuplicateException.class)
    public ResponseEntity<?> handle(BookDuplicateException exception, HttpServletRequest request) {
        ExceptionDetails exceptionDetails = new ExceptionDetails(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handle(DuplicateKeyException exception, HttpServletRequest request) {
        // Unique index yarış durumlarında son savunma hattıdır; kullanıcıya 500 yerine 409 dönüyoruz.
        ExceptionDetails exceptionDetails = new ExceptionDetails(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "Duplicate resource detected.",
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(KafkaEventPublishException.class)
    public ResponseEntity<?> handle(KafkaEventPublishException exception, HttpServletRequest request) {
        ExceptionDetails exceptionDetails = new ExceptionDetails(
                Instant.now().toString(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
