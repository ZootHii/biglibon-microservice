package com.biglibon.sharedlibrary.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic Kafka Event class.
 * Holds information about the consumer group, topic, event type, producer, and the generic payload.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaEvent<T> {
    private String consumerGroup;
    private String topic;
    private String event;
    private String producer;
    private T payload;

    public KafkaEvent(String topic, String event, String producer, T payload) {
        this.topic = topic;
        this.event = event;
        this.producer = producer;
        this.payload = payload;
    }
}

