package com.biglibon.sharedlibrary.producer;

import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void send(KafkaEvent<T> kafkaEvent) {
        try {
            String kafkaEventJson = objectMapper.writeValueAsString(kafkaEvent);
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(kafkaEvent.getTopic(), kafkaEventJson);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("KafkaEventProducer: Failed to publish event to topic {}, event {}, error: {}",
                            kafkaEvent.getTopic(), kafkaEvent.getEvent(), ex.getMessage());
                } else if (result != null) {
                    log.info("KafkaEventProducer: KafkaEvent published; " +
                                    "payload={}, topic={}, event={}, producer={}, partition={}, offset={}",
                            kafkaEventJson,
                            result.getRecordMetadata().topic(),
                            kafkaEvent.getEvent(),
                            kafkaEvent.getProducer(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("KafkaEventProducer: Failed to serialize event", e);
        }
    }
}
