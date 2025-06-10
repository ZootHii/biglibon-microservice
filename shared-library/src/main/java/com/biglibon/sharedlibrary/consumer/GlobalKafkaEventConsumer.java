package com.biglibon.sharedlibrary.consumer;

import com.biglibon.sharedlibrary.constant.KafkaTopics;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GlobalKafkaEventConsumer {

    private final KafkaEventConsumerRegistry kafkaEventConsumerRegistry;
    private final ObjectMapper objectMapper;

    public GlobalKafkaEventConsumer(KafkaEventConsumerRegistry kafkaEventConsumerRegistry, ObjectMapper objectMapper) {
        this.kafkaEventConsumerRegistry = kafkaEventConsumerRegistry;
        this.objectMapper = objectMapper;
    }


    @KafkaListener(
            topics = {KafkaTopics.BS_BOOK_ADDED, KafkaTopics.LS_LIBRARY_UPDATED},
            groupId = "biglibon-consumer-group"
    )
    public void consumeEvent(ConsumerRecord<String, ?> consumerRecord) {
        String topic = consumerRecord.topic();
        Object event = consumerRecord.value();

        kafkaEventConsumerRegistry.getKafkaEvent(topic).ifPresentOrElse(kafkaEvent -> {
            try {
                Object eventData = objectMapper.readValue((JsonParser) event, kafkaEvent);
                kafkaEventConsumerRegistry.getKafkaEventConsumer(topic).ifPresent(kafkaEventConsumer -> {
                    ((KafkaEventConsumer<Object>) kafkaEventConsumer).consumeEvent(eventData);
                });
                log.info("UserCreatedEventConsumer.consumeApprovalRequestResultedEvent consumed EVENT :{} " +
                                "from partition : {} " +
                                "with offset : {} " +
                                "thread : {} " +
                                "for message key: {}",
                        eventData, consumerRecord.partition(), consumerRecord.offset(), Thread.currentThread().getName(), consumerRecord.key());
            } catch (Exception e) {
                System.err.println("Deserialization error: " + e.getMessage());
            }
        }, () -> System.err.println("No kafka event consumer registered for topic: " + topic));
    }
}
