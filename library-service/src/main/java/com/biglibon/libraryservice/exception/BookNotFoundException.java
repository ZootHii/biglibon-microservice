package com.biglibon.libraryservice.exception;

import com.biglibon.libraryservice.client.ExceptionMessage;
import lombok.Getter;

@Getter
public class BookNotFoundException extends RuntimeException {

    private ExceptionMessage exceptionMessage;

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(ExceptionMessage exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public BookNotFoundException(String message, ExceptionMessage exceptionMessage) {
        super(message);
        this.exceptionMessage = exceptionMessage;
    }

}
