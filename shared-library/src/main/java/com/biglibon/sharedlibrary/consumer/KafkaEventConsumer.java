package com.biglibon.sharedlibrary.consumer;

public interface KafkaEventConsumer<T> {
    void consumeEvent(T event);
}
