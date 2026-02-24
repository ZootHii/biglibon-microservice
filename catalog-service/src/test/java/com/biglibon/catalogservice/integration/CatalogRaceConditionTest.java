package com.biglibon.catalogservice.integration;

import com.biglibon.catalogservice.CatalogServiceApplication;
import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.service.CatalogDataService;
import com.biglibon.catalogservice.service.CatalogDomainService;
import com.biglibon.catalogservice.service.CatalogSearchService;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.LibrarySummaryDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@Import(CatalogDomainService.class)
public class CatalogRaceConditionTest {

    static {
        String userDir = System.getProperty("user.dir");
        String rootPath = userDir.endsWith("catalog-service") ? ".." : ".";

        String absoluteCertPath = "file:" + java.nio.file.Paths.get(rootPath, ".elasticsearch/certs/ca/ca.crt")
                .toAbsolutePath().normalize();

        System.setProperty("spring.elasticsearch.ssl.certificate-authorities", absoluteCertPath);
    }

    @MockitoBean
    private CatalogSearchService catalogSearchService;

    @MockitoBean
    private CatalogDataService catalogDataService;

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

    @AfterEach
    void tearDown() {
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

        runConcurrent(50, 200, () -> {
            try {
                catalogService.createOrUpdateCatalog(dto);
            } catch (DuplicateKeyException ignored) {
            }
        });
        Query query = new Query(
                Criteria.where("book.bookId").is("book-1")
                        .and("book.isbn").is("isbn-1")
        );
        long count = mongoTemplate.count(query, Catalog.class);

        assertEquals(1, count);
    }

    @Test
    void race_addLibraryToCatalogBook()
            throws InterruptedException {
        BookSummaryDto book = new BookSummaryDto("book-1", "Book One", 1,
                "Ahmet Test", "Test", "isbn-1");
        LibrarySummaryDto library = new LibrarySummaryDto(58L, "Library One", "Sivas", "58");

        runConcurrent(50, 200, () -> {
            try {
                catalogService.addLibraryToCatalogBook(book, library);
            } catch (DuplicateKeyException ignored) {
            }
        });

        Query query = new Query(
                Criteria.where("book.bookId").is("book-1")
                        .and("book.isbn").is("isbn-1")
        );
        List<Catalog> catalogs = mongoTemplate.find(query, Catalog.class);

        assertEquals(1, catalogs.size());
        assertEquals(1, catalogs.getFirst().getLibraries().size());
        assertEquals(58L, catalogs.getFirst().getLibraries().getFirst().getLibraryId());
    }

    @Test
    void idempotent() {
        BookSummaryDto book = new BookSummaryDto("book-1", "Book One", 1,
                "Ahmet Test", "Test", "isbn-1");
        LibrarySummaryDto library = new LibrarySummaryDto(58L, "Library One", "Sivas", "58");

        for (int i = 0; i < 1000; i++) {
            catalogService.createOrUpdateCatalog(book);
            catalogService.addLibraryToCatalogBook(book, library);
        }

        Query query = new Query(
                Criteria.where("book.bookId").is("book-1")
                        .and("book.isbn").is("isbn-1")
        );
        List<Catalog> catalogs = mongoTemplate.find(query, Catalog.class);

        assertEquals(1, catalogs.size());
        assertEquals(1, catalogs.getFirst().getLibraries().size());
    }

    @Test
    void race_MultiInstance() throws InterruptedException {

        ConfigurableApplicationContext app1 =
                new SpringApplicationBuilder(CatalogServiceApplication.class)
                        .properties("spring.main.web-application-type=none")
                        .run();

        ConfigurableApplicationContext app2 =
                new SpringApplicationBuilder(CatalogServiceApplication.class)
                        .properties("spring.main.web-application-type=none")
                        .run();

        ConfigurableApplicationContext app3 =
                new SpringApplicationBuilder(CatalogServiceApplication.class)
                        .properties("spring.main.web-application-type=none")
                        .run();

        CatalogDomainService s1 = app1.getBean(CatalogDomainService.class);
        CatalogDomainService s2 = app2.getBean(CatalogDomainService.class);
        CatalogDomainService s3 = app3.getBean(CatalogDomainService.class);

        List<CatalogDomainService> services = List.of(s1, s2, s3);

        BookSummaryDto dto = new BookSummaryDto("book-1", "Book", 1, "A", "Y", "isbn-1");

        runConcurrent(50, 200, () -> {
            int index = ThreadLocalRandom.current().nextInt(services.size());
            CatalogDomainService target = services.get(index);
            try {
                target.createOrUpdateCatalog(dto);
            } catch (DuplicateKeyException ignored) { }
        });

        MongoTemplate template = app1.getBean(MongoTemplate.class);
        long count = template.count(Query.query(
                Criteria.where("book.bookId").is("book-1")
                        .and("book.isbn").is("isbn-1")), Catalog.class
        );

        assertEquals(1, count);

        app1.close();
        app2.close();
        app3.close();
    }

    private void runConcurrent(int threadCount, int repeat, Runnable action)
            throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < repeat; j++) {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(5));
                        action.run();
                    } catch (Exception ignored) {
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
    }
}
