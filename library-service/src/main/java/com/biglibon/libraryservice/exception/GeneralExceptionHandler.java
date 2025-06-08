//package com.biglibon.libraryservice.exception;
//
//import com.biglibon.sharedlibrary.exception.BookNotFoundException;
//import com.biglibon.sharedlibrary.exception.ExceptionDetails;
//import com.biglibon.sharedlibrary.exception.LibraryNotFoundException;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.time.Instant;
//
//@RestControllerAdvice
//public class GeneralExceptionHandler {
//
//    @ExceptionHandler(LibraryNotFoundException.class)
//    public ResponseEntity<?> handle(LibraryNotFoundException exception, HttpServletRequest request) {
//        ExceptionDetails exceptionDetails = new ExceptionDetails(
//                Instant.now().toString(),
//                HttpStatus.NOT_FOUND.value(),
//                HttpStatus.NOT_FOUND.getReasonPhrase(),
//                exception.getMessage(),
//                request.getRequestURI()
//        );
//        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(BookNotFoundException.class)
//    public ResponseEntity<?> handle(BookNotFoundException exception, HttpServletRequest request) {
//        ExceptionDetails exceptionDetails = new ExceptionDetails(
//                Instant.now().toString(),
//                HttpStatus.NOT_FOUND.value(),
//                HttpStatus.NOT_FOUND.getReasonPhrase(),
//                exception.getMessage(),
//                request.getRequestURI()
//        );
//        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
//    }
//}
