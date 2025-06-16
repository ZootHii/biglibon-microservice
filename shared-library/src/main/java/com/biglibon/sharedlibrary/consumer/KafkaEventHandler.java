package com.biglibon.sharedlibrary.consumer;

public interface KafkaEventHandler {
    void handle(KafkaEvent<?> kafkaEvent);
}