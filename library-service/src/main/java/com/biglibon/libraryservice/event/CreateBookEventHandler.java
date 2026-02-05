package com.biglibon.libraryservice.event;

import com.biglibon.libraryservice.service.LibraryService;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.consumer.KafkaEventHandler;
import com.biglibon.sharedlibrary.consumer.KafkaEventSubscription;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaEventSubscription(
        consumerGroup = KafkaConstants.Library.CONSUMER_GROUP,
        topic = KafkaConstants.Book.TOPIC,
        event = KafkaConstants.Book.CREATE_BOOK_EVENT
)
public class CreateBookEventHandler implements KafkaEventHandler {

    private final LibraryService libraryService;
    private final ObjectMapper objectMapper;

    public CreateBookEventHandler(LibraryService libraryService, ObjectMapper objectMapper) {
        this.libraryService = libraryService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(KafkaEvent<?> kafkaEvent) {
        try {
            KafkaEvent<BookDto> typedKafkaEvent =
                    objectMapper.convertValue(kafkaEvent, new TypeReference<>() {
                    });

            log.info("handling create-book event in library-service-consumer-group: {}", typedKafkaEvent);

            // logic here
            // not used for now
            // kullanılabilir
            // library e bildirim göndermek şu şu kitaplar yaratıldı diye ki eklesin

        } catch (Exception e) {
            log.error("Failed to process event: {}, exception: {}", KafkaConstants.Book.CREATE_BOOK_EVENT, e.getMessage(), e);
        }
    }
}
