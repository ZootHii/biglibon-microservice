package com.biglibon.bookservice;

import com.biglibon.bookservice.service.BookDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.biglibon.bookservice", "com.biglibon.sharedlibrary"})
public class BookServiceApplication implements CommandLineRunner {

    private final BookDataService bookDataService;

    public BookServiceApplication(BookDataService bookDataService) {
        this.bookDataService = bookDataService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        bookDataService.setData();
    }
}
