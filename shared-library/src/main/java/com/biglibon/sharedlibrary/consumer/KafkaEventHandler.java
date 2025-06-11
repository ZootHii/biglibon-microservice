package com.biglibon.sharedlibrary.consumer;

public interface KafkaEventHandler {
    String getKey(); // e.g. "book-service-group:book-events"
    void handle(String message);
}
