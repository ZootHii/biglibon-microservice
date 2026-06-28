package com.biglibon.sharedlibrary.exception;

public class KafkaEventPublishException extends RuntimeException {
    public KafkaEventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
