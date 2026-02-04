package com.biglibon.sharedlibrary.exception;

import lombok.Getter;

@Getter
public class BookDuplicateException extends RuntimeException {

    public BookDuplicateException(String message) {
        super(message);
    }

    public BookDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

}
