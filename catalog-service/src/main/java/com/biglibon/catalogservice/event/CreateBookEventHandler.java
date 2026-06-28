package com.biglibon.catalogservice.event;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.service.CatalogDomainService;
import com.biglibon.catalogservice.service.CatalogSearchService;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.consumer.KafkaEventHandler;
import com.biglibon.sharedlibrary.consumer.KafkaEventSubscription;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaEventSubscription(
        consumerGroup = KafkaConstants.Catalog.CONSUMER_GROUP,
        topic = KafkaConstants.Book.TOPIC,
        event = KafkaConstants.Book.CREATE_BOOK_EVENT
)
public class CreateBookEventHandler implements KafkaEventHandler {

    private final CatalogDomainService domainService;
    private final CatalogSearchService searchService;
    private final CatalogMapper catalogMapper;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

    public CreateBookEventHandler(CatalogDomainService domainService, CatalogSearchService searchService,
                                        CatalogMapper catalogMapper, ObjectMapper objectMapper, CacheManager cacheManager) {
        this.domainService = domainService;
        this.searchService = searchService;
        this.catalogMapper = catalogMapper;
        this.objectMapper = objectMapper;
        this.cacheManager = cacheManager;
    }

    // need outbox pattern
    @Override
    public void handle(KafkaEvent<?> kafkaEvent) {
        try {
            KafkaEvent<BookDto> typedKafkaEvent =
                    objectMapper.convertValue(kafkaEvent, new TypeReference<>() {
                    });

            //log.info("handling create-book event in catalog-service-consumer-group: {}", typedKafkaEvent);

            // logic here / each book created or updated event update catalog
            BookDto bookDto = typedKafkaEvent.getPayload();
            BookSummaryDto summary = catalogMapper.bookDtoToBookSummaryDto(bookDto);

            Catalog catalog = domainService.createOrUpdateCatalog(summary);

            searchService.saveCatalogIndex(catalog);

            evictCaches();

        } catch (Exception e) {
            log.error("Failed to process event: {}, exception: {}",
                    KafkaConstants.Book.CREATE_BOOK_EVENT, e.getMessage(), e);
            // Handler hatasını saklamıyoruz ki listener retry mekanizmasına bırakabilsin.
            throw new IllegalStateException("Create book event could not be processed.", e);
        }
    }

    private void evictCaches() {
        clear("catalog-mongo-cache");
        clear("catalog-elasticsearch-cache");
    }

    private void clear(String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache != null) {
            cache.clear();
        }
    }
}
