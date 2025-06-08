package com.biglibon.sharedlibrary.exception;

import lombok.Getter;

@Getter
public class LibraryNotFoundException extends RuntimeException {

    public LibraryNotFoundException(String message) {
        super(message);
    }

    public LibraryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
