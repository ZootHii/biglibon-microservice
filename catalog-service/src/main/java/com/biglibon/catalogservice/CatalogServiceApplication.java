package com.biglibon.catalogservice;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.repository.CatalogRepository;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.LibrarySummaryDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.biglibon.catalogservice","com.biglibon.sharedlibrary"})
public class CatalogServiceApplication implements CommandLineRunner {

    private final CatalogRepository catalogRepository;

    public CatalogServiceApplication(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        BookSummaryDto bookSummaryDto1 = new BookSummaryDto("68462fc5374b250b3afeff93", "Sineklerin Tanrısı", 1954, "William Golding", "Kültür Yayınları", "111");
        BookSummaryDto bookSummaryDto2 = new BookSummaryDto("68462fc5374b250b3afeff94", "Hamlet", 1602, "William Shakespeare", "Ren Yayınları", "222");
        LibrarySummaryDto librarySummaryDto1 = new LibrarySummaryDto(1L, "Şems-i Sivasî İl Halk Kütüphanesi", "Sivas", "(0346) 221 11 12");
        LibrarySummaryDto librarySummaryDto2 = new LibrarySummaryDto(2L, "Milli Kütüphane", "Ankara", "(0312) 470 83 83");
        Catalog catalog1 = new Catalog(bookSummaryDto1, List.of(librarySummaryDto1));
        Catalog catalog2 = new Catalog(bookSummaryDto2, List.of(librarySummaryDto1, librarySummaryDto2));
        try {
            System.out.println("Saved Catalogs: " + catalogRepository.saveAll(List.of(catalog1, catalog2)));

        } catch (Exception e) {
            System.out.println("Skipping duplicate entry: " + e.getMessage());
        }
    }
}
