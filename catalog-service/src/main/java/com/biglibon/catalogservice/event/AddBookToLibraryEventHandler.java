package com.biglibon.catalogservice.event;

import com.biglibon.catalogservice.service.CatalogEventService;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.consumer.KafkaEventHandler;
import com.biglibon.sharedlibrary.consumer.KafkaEventSubscription;
import com.biglibon.sharedlibrary.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaEventSubscription(
        consumerGroup = KafkaConstants.Catalog.CONSUMER_GROUP,
        topic = KafkaConstants.Library.TOPIC,
        event = KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT
)
public class AddBookToLibraryEventHandler implements KafkaEventHandler {

    private final CatalogEventService catalogEventService;
    private final ObjectMapper objectMapper;

    public AddBookToLibraryEventHandler(CatalogEventService catalogEventService, ObjectMapper objectMapper) {
        this.catalogEventService = catalogEventService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(KafkaEvent<?> kafkaEvent) {
        try {
            KafkaEvent<LibraryDto> typedKafkaEvent =
                    objectMapper.convertValue(kafkaEvent, new TypeReference<>() {
                    });

            //log.info("handling add-book-to-library event in catalog-service-consumer-group: {}", typedKafkaEvent);

            // logic here
            // if there is a book with libraries in library-service but no catalog yet
            // create new catalog and update libraries then save

            LibraryDto libraryDto = typedKafkaEvent.getPayload();
            catalogEventService.mapLibraryDtoToSummaryDtos(libraryDto);

        } catch (Exception e) {
            log.error("Failed to process event: {}, exception: {}",
                    KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT, e.getMessage(), e);
        }
    }
}
