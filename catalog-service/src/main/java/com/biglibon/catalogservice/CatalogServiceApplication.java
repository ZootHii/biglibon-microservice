package com.biglibon.catalogservice;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.repository.CatalogMongoRepository;
import com.biglibon.catalogservice.service.CatalogSearchService;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.LibrarySummaryDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.biglibon.catalogservice", "com.biglibon.sharedlibrary"})
public class CatalogServiceApplication implements CommandLineRunner {

    private final CatalogMongoRepository catalogMongoRepository;
    private final CatalogSearchService catalogSearchService;

    public CatalogServiceApplication(CatalogMongoRepository catalogMongoRepository, CatalogSearchService catalogSearchService) {
        this.catalogMongoRepository = catalogMongoRepository;
        this.catalogSearchService = catalogSearchService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        BookSummaryDto bookSummaryDto1 = new BookSummaryDto("9998", "9998 Title", 9998, "9998 Author", "9998 Publisher", "9998isbn");
        BookSummaryDto bookSummaryDto2 = new BookSummaryDto("9999", "9999 Title", 9999, "9999 Author", "9999 Publisher", "9999isbn");
        LibrarySummaryDto librarySummaryDto1 = new LibrarySummaryDto(9998L, "9998 Dummy Library", "9998 City", "9998 Phone");
        LibrarySummaryDto librarySummaryDto2 = new LibrarySummaryDto(9999L, "9999 Dummy Library", "9999 City", "9999 Phone");
        Catalog catalog1 = new Catalog(bookSummaryDto1, List.of(librarySummaryDto1));
        Catalog catalog2 = new Catalog(bookSummaryDto2, List.of(librarySummaryDto1, librarySummaryDto2));
        try {
            List<Catalog> catalogs = List.of(catalog1, catalog2);
            System.out.println("Saved Catalogs: " + catalogMongoRepository.saveAll(catalogs));
            catalogSearchService.saveCatalogIndices(catalogs);
        } catch (Exception e) {
            System.out.println("Skipping duplicate entry: " + e.getMessage());
        }
    }
}
