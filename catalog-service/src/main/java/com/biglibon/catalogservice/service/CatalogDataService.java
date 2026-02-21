package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.repository.CatalogMongoRepository;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.LibrarySummaryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CatalogDataService {

    private final CatalogMongoRepository repository;
    private final CatalogSearchService searchService;

    public CatalogDataService(CatalogMongoRepository repository, CatalogSearchService searchService) {
        this.repository = repository;
        this.searchService = searchService;
    }

    public void setData() {
        if (repository.count() == 0) {
            firstDataSet();
        } else {
            log.info("data exists, clear if you want to reset data set");
        }
    }

    public void firstDataSet() {
        log.info("setting first data set");
        BookSummaryDto bookSummaryDto1 = new BookSummaryDto("9998", "9998 Title", 9998, "9998 Author", "9998 Publisher", "9998isbn");
        BookSummaryDto bookSummaryDto2 = new BookSummaryDto("9999", "9999 Title", 9999, "9999 Author", "9999 Publisher", "9999isbn");
        LibrarySummaryDto librarySummaryDto1 = new LibrarySummaryDto(9998L, "9998 Dummy Library", "9998 City", "9998 Phone");
        LibrarySummaryDto librarySummaryDto2 = new LibrarySummaryDto(9999L, "9999 Dummy Library", "9999 City", "9999 Phone");
        Catalog catalog1 = new Catalog(bookSummaryDto1, List.of(librarySummaryDto1));
        Catalog catalog2 = new Catalog(bookSummaryDto2, List.of(librarySummaryDto1, librarySummaryDto2));
        try {
            List<Catalog> catalogs = List.of(catalog1, catalog2);
            System.out.println("Saved Catalogs: " + repository.saveAll(catalogs));
            searchService.saveCatalogIndices(catalogs);
        } catch (Exception e) {
            System.out.println("Skipping duplicate entry: " + e.getMessage());
        }
    }
}
