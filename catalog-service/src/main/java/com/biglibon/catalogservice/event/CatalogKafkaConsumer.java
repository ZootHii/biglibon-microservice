package com.biglibon.catalogservice.event;

import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.consumer.KafkaEventDispatcher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CatalogKafkaConsumer {

    private final KafkaEventDispatcher dispatcher;
    private final ObjectMapper objectMapper;

    public CatalogKafkaConsumer(KafkaEventDispatcher dispatcher, ObjectMapper objectMapper) {
        this.dispatcher = dispatcher;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = {
                    KafkaConstants.Book.TOPIC,
                    KafkaConstants.Library.TOPIC
            },
            groupId = KafkaConstants.Catalog.CONSUMER_GROUP
    )
    public void listen(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String kafkaEventJson = record.value();
        try {
            KafkaEvent<?> kafkaEvent =
                    objectMapper.readValue(kafkaEventJson, new TypeReference<>() {
                    });
            kafkaEvent.setConsumerGroup(KafkaConstants.Catalog.CONSUMER_GROUP);
            log.info("KafkaEvent received | Topic: {}, Event: {}, ConsumerGroup: {}, Payload: {}",
                    topic, kafkaEvent.getEvent(), KafkaConstants.Catalog.CONSUMER_GROUP, kafkaEvent.getPayload());
            dispatcher.dispatch(kafkaEvent);

        } catch (Exception e) {
            log.error("Kafka message processing failed: {}", e.getMessage(), e);
        }
    }
}

