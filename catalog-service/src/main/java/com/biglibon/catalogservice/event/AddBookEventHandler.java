package com.biglibon.catalogservice.event;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.service.CatalogService;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.consumer.KafkaEventHandler;
import com.biglibon.sharedlibrary.consumer.KafkaEventSubscription;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.CatalogDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaEventSubscription(
        consumerGroup = KafkaConstants.Catalog.CONSUMER_GROUP,
        topic = KafkaConstants.Book.TOPIC,
        event = KafkaConstants.Book.ADD_BOOK_EVENT
)
public class AddBookEventHandler implements KafkaEventHandler {

    private final CatalogService catalogService;
    private final CatalogMapper catalogMapper;
    private final ObjectMapper objectMapper;

    public AddBookEventHandler(
            CatalogService catalogService,
            CatalogMapper catalogMapper,
            ObjectMapper objectMapper) {
        this.catalogService = catalogService;
        this.catalogMapper = catalogMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(KafkaEvent<?> kafkaEvent) {
        try {
            KafkaEvent<BookDto> typedKafkaEvent =
                    objectMapper.convertValue(kafkaEvent, new TypeReference<>() {
                    });

            //log.info("handling book-added event in catalog-service-consumer-group: {}", typedKafkaEvent);

            // logic here
            BookSummaryDto bookSummaryDto = catalogMapper.bookDtoToBookSummaryDto(typedKafkaEvent.getPayload());

            CatalogDto catalogDto = catalogService.addOrUpdateBook(bookSummaryDto);
            log.info("addOrUpdateBook in catalog: {}", catalogDto);

        } catch (Exception e) {
            log.error("Failed to process event: {}, exception: {}",
                    KafkaConstants.Book.ADD_BOOK_EVENT, e.getMessage(), e);
        }
    }
}
