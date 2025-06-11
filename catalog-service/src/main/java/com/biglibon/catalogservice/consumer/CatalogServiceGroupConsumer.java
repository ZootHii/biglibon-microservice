package com.biglibon.catalogservice.consumer;

import com.biglibon.sharedlibrary.consumer.KafkaConsumer;
import com.biglibon.sharedlibrary.consumer.KafkaEventDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CatalogServiceGroupConsumer extends KafkaConsumer {

    public CatalogServiceGroupConsumer(KafkaEventDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void consume(ConsumerRecord<String, String> record) {
        log.info("CONSUME: CatalogServiceGroupConsumer record {}", record);
        dispatcher.dispatch("catalog-service-consumer-group", record.topic(), record.value());
    }

    // Separate method to be recognized by Spring as listener
    @KafkaListener(topics = "book-service.book-added", groupId = "catalog-service-consumer-group")
    public void listen(ConsumerRecord<String, String> record) {
        log.info("CONSUME: CatalogServiceGroupConsumer listen record {}", record);
        consume(record);  // delegate to abstract method implementation
    }
}
