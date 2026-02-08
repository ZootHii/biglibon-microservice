package com.biglibon.bookservice.service;

import com.biglibon.bookservice.mapper.BookMapper;
import com.biglibon.bookservice.model.Book;
import com.biglibon.bookservice.repository.BookRepository;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * To create real data using Openlibrary API
 */
@Slf4j
@Service
public class BookDataService {

    private final BookRepository repository;
    private final BookMapper bookMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public BookDataService(BookRepository repository, BookMapper bookMapper,
                           KafkaEventProducer kafkaEventProducer) {
        this.repository = repository;
        this.bookMapper = bookMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    public void setData() {
        if (repository.count() == 0) {
            firstDataSet();
            fetchAndCreateBooks();
        } else {
            log.info("data exists, clear if you want to reset data set");
        }
    }

    public void firstDataSet() {
        log.info("setting first data set");
        Book book1 = new Book("Sineklerin Tanrısı", 1954, "William Golding", "Kültür Yayınları", "111");
        Book book2 = new Book("Hamlet", 1602, "William Shakespeare", "Ren Yayınları", "222");
        Book book3 = new Book("Cesur Yeni Dünya", 1932, "Aldous Huxley", "İthaki Yayınları", "333");
        Book book4 = new Book("Masumiyet Müzesi", 2008, "Orhan Pamuk", "YKY", "444");

        List<Book> books = List.of(book1, book2, book3, book4);
        log.info("Saved Books: {}", repository.saveAll(books));
        books.forEach(book -> {
            kafkaEventProducer.send(new KafkaEvent<>(
                    KafkaConstants.Book.TOPIC,
                    KafkaConstants.Book.CREATE_BOOK_EVENT,
                    KafkaConstants.Book.PRODUCER,
                    bookMapper.toDto(book)));
        });
    }

    public void fetchAndCreateBooks() {
        RestTemplate restTemplate = new RestTemplate();

        String author;
        Integer publicationYear;
        String title;
        String publisher;
        String isbn;

        int count = 0;
        int page = 1;

        log.info("fetching and saving real data set and sending kafka book-create event");
        while (count < 1000) {
            String API_URL = "https://openlibrary.org/search.json?q=language:tur&fields=key,author_name,editions,editions.key,editions.title,first_publish_year,editions.publisher&limit=500&page=" + page;

            JsonNode root = restTemplate.getForObject(API_URL, JsonNode.class);
            JsonNode docs = root.path("docs");

            if (docs.isEmpty()) {
                log.warn("no more data available from API stopped at {}", count);
                break;
            }
            for (JsonNode doc : docs) {
                if (count >= 1000) break;

                publicationYear = doc.has("first_publish_year") ? doc.path("first_publish_year").asInt() : null;
                author = doc.path("author_name").has(0) ? doc.path("author_name").get(0).asText() : null;

                JsonNode editions = doc.path("editions").path("docs");

                for (JsonNode ed : editions) {
                    if (count >= 1000) break;

                    title = ed.path("title").asText();
                    publisher = ed.path("publisher").has(0) ? ed.path("publisher").get(0).asText() : null;
                    isbn = ed.path("key").asText();

                    if (title != null && author != null && publicationYear != null && publisher != null && isbn != null) {
                        Book book = new Book(title, publicationYear, author, publisher, isbn);

                        Book bookSaved = repository.save(book);
                        BookDto bookSavedDto = bookMapper.toDto(bookSaved);

                        kafkaEventProducer.send(new KafkaEvent<>(
                                KafkaConstants.Book.TOPIC,
                                KafkaConstants.Book.CREATE_BOOK_EVENT,
                                KafkaConstants.Book.PRODUCER,
                                bookSavedDto));

                        count++;
                    }
                }
            }
            page++;
        }
    }
}
