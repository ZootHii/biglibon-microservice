package com.biglibon.catalogservice.integration;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.service.CatalogDomainService;
import com.biglibon.catalogservice.service.CatalogSearchService;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@Import(CatalogDomainService.class)
public class CatalogRaceConditionTest {

    @MockitoBean
    private CatalogSearchService catalogSearchService;

    @Autowired
    private CatalogDomainService catalogService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void cleanDB() {
        Query query = new Query(
                Criteria.where("book.bookId").is("book-1")
                        .and("book.isbn").is("isbn-1")
        );
        mongoTemplate.remove(query, Catalog.class);
    }

    @Test
    void race_createOrUpdateCatalog() throws InterruptedException {
        BookSummaryDto dto = new BookSummaryDto("book-1","Book One", 1,
                "Ahmet Test", "Test", "isbn-1");

        int threadCount = 2; // aynı anda iki event simüle
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    catalogService.createOrUpdateCatalog(dto);
                } catch (Exception e) {
                    System.out.println("Exception caught: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long count = mongoTemplate.count(new Query(), Catalog.class);
        System.out.println("Catalog count: " + count);
        assertEquals(1, count); // unique index sayesinde sadece 1 catalog olmalı
    }
}