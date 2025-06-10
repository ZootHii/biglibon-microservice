package com.biglibon.catalogservice.consumer;

import com.biglibon.sharedlibrary.constant.KafkaTopics;
import com.biglibon.sharedlibrary.consumer.KafkaEventConsumer;
import com.biglibon.sharedlibrary.consumer.KafkaEventConsumerRegistry;
import com.biglibon.sharedlibrary.dto.BookDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookAddedEventConsumer implements KafkaEventConsumer<BookDto> {

    private final KafkaEventConsumerRegistry consumerRegistry;

    public BookAddedEventConsumer(KafkaEventConsumerRegistry consumerRegistry) {
        this.consumerRegistry = consumerRegistry;
    }

    @PostConstruct
    public void register() {
        consumerRegistry.register(KafkaTopics.BS_BOOK_ADDED, BookDto.class, this);
        log.info("BookAddedEventConsumer registered for topic {}", KafkaTopics.BS_BOOK_ADDED);
    }

    @Override
    public void consumeEvent(BookDto event) {
        log.info("Consume Book Added Event: {}", event);
    }
}
