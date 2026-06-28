package com.biglibon.bookservice.integration;

import com.biglibon.bookservice.mapper.BookMapper;
import com.biglibon.bookservice.model.Book;
import com.biglibon.bookservice.service.BookDataService;
import com.biglibon.bookservice.service.BookService;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.BookDuplicateException;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.junit.jupiter.api.AfterEach;
import org.springframework.dao.DuplicateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataMongoTest
@Import(BookService.class)
class BookRaceConditionTest {

    @MockitoBean
    private KafkaEventProducer kafkaEventProducer;

    @MockitoBean
    private BookMapper bookMapper;

    @MockitoBean
    private BookDataService bookDataService;

    @Autowired
    private BookService bookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void cleanDB() {
        mongoTemplate.remove(new Query(Criteria.where("isbn").is("race-isbn-1")), Book.class);

        when(bookMapper.toEntity(any(BookDto.class))).thenAnswer(invocation -> {
            BookDto dto = invocation.getArgument(0);
            return new Book(dto.title(), dto.publicationYear(), dto.author(), dto.publisher(), dto.isbn());
        });
        when(bookMapper.toDto(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return new BookDto(book.getId(), book.getTitle(), book.getPublicationYear(), book.getAuthor(), book.getPublisher(),
                    book.getIsbn(), book.getCreatedAt(), book.getUpdatedAt());
        });
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.remove(new Query(Criteria.where("isbn").is("race-isbn-1")), Book.class);
    }

    @Test
    void race_create_whenSameIsbnConcurrently_onlyOneBookIsCreated() throws InterruptedException {
        BookDto dto = new BookDto(null, "Race Book", 2026, "Race Author",
                "Race Publisher", "race-isbn-1", null, null);

        runConcurrent(2, () -> {
            try {
                bookService.create(dto);
            } catch (BookDuplicateException | DuplicateKeyException ignored) {
            }
        });

        Query query = new Query(Criteria.where("isbn").is("race-isbn-1"));
        long count = mongoTemplate.count(query, Book.class);
        assertEquals(1, count);
        verify(kafkaEventProducer, atMost(1)).sendAndWait(any());
    }

    private void runConcurrent(int threadCount, Runnable action) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        AtomicReference<RuntimeException> exceptionRef = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await();
                    action.run();
                } catch (RuntimeException e) {
                    exceptionRef.compareAndSet(null, e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    exceptionRef.compareAndSet(null, new RuntimeException(e));
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        if (exceptionRef.get() != null) {
            throw exceptionRef.get();
        }
    }
}
