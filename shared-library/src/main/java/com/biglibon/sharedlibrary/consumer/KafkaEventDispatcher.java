package com.biglibon.sharedlibrary.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KafkaEventDispatcher {

    private final List<KafkaEventHandler> kafkaEventHandlers;

    public KafkaEventDispatcher(List<KafkaEventHandler> kafkaEventHandlers) {
        this.kafkaEventHandlers = kafkaEventHandlers;
    }

    public void dispatch(KafkaEvent<?> kafkaEvent) {
        for (KafkaEventHandler kafkaEventHandler : kafkaEventHandlers) {
            KafkaEventSubscription kafkaEventSubscription =
                    kafkaEventHandler.getClass().getAnnotation(KafkaEventSubscription.class);

            if (kafkaEventSubscription != null &&
                    kafkaEventSubscription.topic().equals(kafkaEvent.getTopic()) &&
                    kafkaEventSubscription.event().equals(kafkaEvent.getEvent()) &&
                    kafkaEventSubscription.consumerGroup().equals(kafkaEvent.getConsumerGroup())) {

                kafkaEventHandler.handle(kafkaEvent);
                return;
            }
        }
        log.warn("No KafkaEventHandler found for KafkaEvent: {}", kafkaEvent);
    }
}