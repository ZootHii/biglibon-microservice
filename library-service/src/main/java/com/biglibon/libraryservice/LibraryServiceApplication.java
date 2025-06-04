package com.biglibon.libraryservice;

import com.biglibon.libraryservice.client.BookServiceClient;
import com.biglibon.libraryservice.model.Library;
import com.biglibon.libraryservice.repository.LibraryRepository;
import com.biglibon.sharedlibrary.dto.BookDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableFeignClients
public class LibraryServiceApplication implements CommandLineRunner {

    private final LibraryRepository libraryRepository;
    private final BookServiceClient bookServiceClient;

    public LibraryServiceApplication(LibraryRepository libraryRepository, BookServiceClient bookServiceClient) {
        this.libraryRepository = libraryRepository;
        this.bookServiceClient = bookServiceClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (libraryRepository.findAll().isEmpty()) {
            Thread.sleep(58000); // little help for getting up book-service
            Library library1 = new Library("Şems-i Sivasî İl Halk Kütüphanesi", "Sivas", "(0346) 221 11 12");
            Library library2 = new Library("Milli Kütüphane", "Ankara", "(0312) 470 83 83",
                    List.of(Optional.ofNullable(bookServiceClient.getByIsbn("444").getBody())
                            .map(BookDto::id).orElse(":D")));

            libraryRepository.saveAll(List.of(library1, library2));
        }
    }
}
