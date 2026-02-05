package com.biglibon.bookservice;

import com.biglibon.bookservice.mapper.BookMapper;
import com.biglibon.bookservice.model.Book;
import com.biglibon.bookservice.repository.BookRepository;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.biglibon.bookservice", "com.biglibon.sharedlibrary"})
public class BookServiceApplication implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final BookMapper bookMapper;

    public BookServiceApplication(BookRepository bookRepository, KafkaEventProducer kafkaEventProducer, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.kafkaEventProducer = kafkaEventProducer;
        this.bookMapper = bookMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Book book1 = new Book("Sineklerin Tanrısı", 1954, "William Golding", "Kültür Yayınları", "111");
        Book book2 = new Book("Hamlet", 1602, "William Shakespeare", "Ren Yayınları", "222");
        Book book3 = new Book("Cesur Yeni Dünya", 1932, "Aldous Huxley", "İthaki Yayınları", "333");
        Book book4 = new Book("Masumiyet Müzesi", 2008, "Orhan Pamuk", "YKY", "444");
        try {
            List<Book> books = List.of(book1, book2, book3, book4);
            System.out.println("Saved Books: " + bookRepository.saveAll(books));
            books.forEach(book -> {
                kafkaEventProducer.send(new KafkaEvent<>(
                        KafkaConstants.Book.TOPIC,
                        KafkaConstants.Book.CREATE_BOOK_EVENT,
                        KafkaConstants.Book.PRODUCER,
                        bookMapper.toDto(book)));
            });
        } catch (Exception e) {
            System.out.println("Skipping duplicate entry: " + e.getMessage());
        }
    }
}
