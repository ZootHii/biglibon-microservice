package com.biglibon.bookservice;

import com.biglibon.bookservice.service.BookDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.biglibon.bookservice", "com.biglibon.sharedlibrary"})
public class BookServiceApplication implements CommandLineRunner {

    private final BookDataService bookDataService;
    private final boolean seedEnabled;

    public BookServiceApplication(BookDataService bookDataService,
                                  @Value("${biglibon.seed.enabled:false}") boolean seedEnabled) {
        this.bookDataService = bookDataService;
        this.seedEnabled = seedEnabled;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            // Seed varsayılan kapalı; servis startup'ı dış API/Kafka durumuna bağlı kalmasın.
            log.info("Book seed disabled. Set biglibon.seed.enabled=true to create sample data.");
            return;
        }
        bookDataService.setData();
    }
}
