package com.biglibon.catalogservice.event;

import com.biglibon.catalogservice.service.CatalogEventService;
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
        consumerGroup = KafkaConstants.Catalog.CONSUMER_GROUP,
        topic = KafkaConstants.Book.TOPIC,
        event = KafkaConstants.Book.CREATE_BOOK_EVENT
)
public class CreateBookEventHandler implements KafkaEventHandler {

    private final CatalogEventService catalogEventService;
    private final ObjectMapper objectMapper;

    public CreateBookEventHandler(CatalogEventService catalogEventService, ObjectMapper objectMapper) {
        this.catalogEventService = catalogEventService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(KafkaEvent<?> kafkaEvent) {
        try {
            KafkaEvent<BookDto> typedKafkaEvent =
                    objectMapper.convertValue(kafkaEvent, new TypeReference<>() {
                    });

            //log.info("handling create-book event in catalog-service-consumer-group: {}", typedKafkaEvent);

            // logic here / each book created or updated event update catalog
            BookDto bookDto = typedKafkaEvent.getPayload();
            catalogEventService.createOrUpdateCatalog(bookDto);

        } catch (Exception e) {
            log.error("Failed to process event: {}, exception: {}",
                    KafkaConstants.Book.CREATE_BOOK_EVENT, e.getMessage(), e);
        }
    }
}
