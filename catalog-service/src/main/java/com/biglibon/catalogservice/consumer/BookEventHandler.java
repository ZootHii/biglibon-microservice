package com.biglibon.catalogservice.consumer;

import com.biglibon.sharedlibrary.consumer.KafkaEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class BookEventHandler implements KafkaEventHandler {
    @Override
    public String getKey() {
        return "catalog-service-consumer-group:book-service.book-added";
    }

    @Override
    public void handle(String message) {
        log.info("handling book event: {}", message);
    }
}
