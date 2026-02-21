package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.repository.CatalogMongoRepository;
import com.biglibon.sharedlibrary.dto.*;
import com.biglibon.sharedlibrary.performance.TrackPerformanceMetric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
// change only state and publish application events
public class CatalogDomainService {

    private final CatalogMongoRepository repository;

    public CatalogDomainService(CatalogMongoRepository repository) {
        this.repository = repository;
    }

    @TrackPerformanceMetric
    public Catalog createOrUpdateCatalog(BookSummaryDto bookSummaryDto) {
        Catalog catalog = repository
                .findByBookBookIdOrBookIsbn(
                        bookSummaryDto.getBookId(),
                        bookSummaryDto.getIsbn()
                )
                .map(existing -> { // update book
                    existing.setBook(bookSummaryDto);
                    return existing;
                })
                // if there is a book in book-service but no catalog yet
                .orElseGet(() ->
                        new Catalog(bookSummaryDto, new ArrayList<>())
                );

        catalog = repository.save(catalog);

        return catalog;
    }

    @TrackPerformanceMetric
    public Catalog addLibraryToCatalogBook(BookSummaryDto bookSummaryDto,
                                           LibrarySummaryDto librarySummaryDto) {

        Catalog catalog = repository
                .findByBookBookIdOrBookIsbn(
                        bookSummaryDto.getBookId(),
                        bookSummaryDto.getIsbn()
                )
                .orElseGet(() ->
                        new Catalog(bookSummaryDto, new ArrayList<>())
                );

        boolean exists = catalog.getLibraries()
                .stream()
                .anyMatch(lib ->
                        lib.getLibraryId()
                                .equals(librarySummaryDto.getLibraryId())
                );

        if (!exists) {
            catalog.getLibraries().add(librarySummaryDto);
            catalog = repository.save(catalog);
        }

        return catalog;
    }
}
