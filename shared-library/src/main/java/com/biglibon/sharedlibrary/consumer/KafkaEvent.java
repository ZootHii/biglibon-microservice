package com.biglibon.sharedlibrary.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

