package com.biglibon.libraryservice;

import com.biglibon.libraryservice.model.Library;
import com.biglibon.libraryservice.repository.LibraryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableFeignClients
public class LibraryServiceApplication implements CommandLineRunner {

    private final LibraryRepository libraryRepository;

    public LibraryServiceApplication(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Library library1 = new Library("Şems-i Sivasî İl Halk Kütüphanesi", "Sivas", "(0346) 221 11 12");
        Library library2 = new Library("Milli Kütüphane", "Ankara", "(0312) 470 83 83", List.of("683d9f2a2f8be770e0ae06f2"));

        libraryRepository.saveAll(List.of(library1, library2));
    }
}
