package com.biglibon.sharedlibrary.exception;

import lombok.Getter;

@Getter
public class LibraryDuplicateException extends RuntimeException {

    public LibraryDuplicateException(String message) {
        super(message);
    }

    public LibraryDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

}
