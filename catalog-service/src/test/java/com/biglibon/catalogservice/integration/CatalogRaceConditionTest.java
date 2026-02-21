package com.biglibon.catalogservice.integration;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.service.CatalogDomainService;
import com.biglibon.catalogservice.service.CatalogSearchService;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.LibrarySummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

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
        BookSummaryDto dto = new BookSummaryDto("book-1", "Book One", 1,
                "Ahmet Test", "Test", "isbn-1");

        runConcurrent(2, () -> catalogService.createOrUpdateCatalog(dto));

        long count = mongoTemplate.count(new Query(), Catalog.class);
        assertEquals(1, count);
    }

    @Test
    void race_addLibraryToCatalogBook()
            throws InterruptedException {
        BookSummaryDto book = new BookSummaryDto("book-1","Book One", 1,
                "Ahmet Test", "Test", "isbn-1");
        LibrarySummaryDto library = new LibrarySummaryDto(58L, "Library One", "Sivas", "58");

        runConcurrent(2, () -> catalogService.addLibraryToCatalogBook(book, library));

        List<Catalog> catalogs = mongoTemplate.find(new Query(), Catalog.class);
        assertEquals(1, catalogs.size());
        assertEquals(1, catalogs.getFirst().getLibraries().size());
        assertEquals(58L, catalogs.getFirst().getLibraries().getFirst().getLibraryId());
    }

    private void runConcurrent(int threadCount, Runnable action) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicReference<RuntimeException> exceptionRef = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {

                    action.run();
                } catch (RuntimeException e) {
                    exceptionRef.compareAndSet(null, e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        if (exceptionRef.get() != null) {
            throw exceptionRef.get();
        }
    }

}