package com.biglibon.catalogservice.event;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.service.CatalogService;
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

    private final CatalogService catalogService;
    private final CatalogMapper catalogMapper;
    private final ObjectMapper objectMapper;

    public AddBookToLibraryEventHandler(CatalogService catalogService, CatalogMapper catalogMapper, ObjectMapper objectMapper) {
        this.catalogService = catalogService;
        this.catalogMapper = catalogMapper;
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
            LibraryDto libraryDto = typedKafkaEvent.getPayload();
            LibrarySummaryDto librarySummaryDto = catalogMapper.libraryDtoToLibrarySummaryDto(libraryDto);

            libraryDto.getBooks().forEach(bookDto -> {
                CatalogDto catalogDto = catalogService.addLibraryToBook(
                        catalogMapper.bookDtoToBookSummaryDto(bookDto),
                        librarySummaryDto
                );
                log.info("addLibraryToBook in catalog: {}", catalogDto);
            });

        } catch (Exception e) {
            log.error("Failed to process add-book-to-library event: {}", e.getMessage(), e);
        }
    }
}
