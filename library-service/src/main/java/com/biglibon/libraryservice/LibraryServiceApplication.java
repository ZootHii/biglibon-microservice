package com.biglibon.libraryservice;

import com.biglibon.libraryservice.service.LibraryService;
import com.biglibon.sharedlibrary.client.BookServiceClient;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.libraryservice.repository.LibraryRepository;
import com.biglibon.sharedlibrary.constant.KafkaConstants;
import com.biglibon.sharedlibrary.consumer.KafkaEvent;
import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.producer.KafkaEventProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;

@EnableFeignClients(clients = BookServiceClient.class)
@SpringBootApplication(scanBasePackages = {"com.biglibon.libraryservice", "com.biglibon.sharedlibrary"})
public class LibraryServiceApplication implements CommandLineRunner {

    private final LibraryRepository libraryRepository;
    private final BookServiceClient bookServiceClient;
    private final KafkaEventProducer kafkaEventProducer;
    private final LibraryService libraryService;

    public LibraryServiceApplication(LibraryRepository libraryRepository, BookServiceClient bookServiceClient,
                                     KafkaEventProducer kafkaEventProducer, LibraryService libraryService) {
        this.libraryRepository = libraryRepository;
        this.bookServiceClient = bookServiceClient;
        this.kafkaEventProducer = kafkaEventProducer;
        this.libraryService = libraryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        initialize();
    }

    public void initialize() throws InterruptedException {
        String bookId;
        while (true) {
            BookDto bookDto = bookServiceClient.getByIsbn("444").getBody();
            if (bookDto != null) {
                bookId = bookDto.id();
                break;
            }
            Thread.sleep(5000);
        }

        Library library1 = new Library("Şems-i Sivasî İl Halk Kütüphanesi", "Sivas", "(0346) 221 11 12");
        Library library2 = new Library("Milli Kütüphane", "Ankara", "(0312) 470 83 83", List.of(bookId));
        try {
            List<Library> libraries = List.of(library1, library2);
            System.out.println("Saved Libraries: " + libraryRepository.saveAll(libraries));

            kafkaEventProducer.send(new KafkaEvent<>(
                    KafkaConstants.Library.TOPIC,
                    KafkaConstants.Library.ADD_BOOK_TO_LIBRARY_EVENT,
                    KafkaConstants.Library.PRODUCER,
                    libraryService.replaceBookIdsWithBooks(library2)));

        } catch (Exception e) {
            System.out.println("Skipping duplicate entry: " + e.getMessage());
        }
    }
}
