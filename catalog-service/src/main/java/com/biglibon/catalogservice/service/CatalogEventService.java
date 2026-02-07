package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.repository.CatalogMongoRepository;
import com.biglibon.sharedlibrary.dto.*;
import com.biglibon.sharedlibrary.performance.TrackPerformanceMetric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CatalogEventService {

    private final CatalogMongoRepository catalogMongoRepository;
    private final CatalogSearchService catalogSearchService;
    private final CatalogMapper catalogMapper;

    public CatalogEventService(CatalogMongoRepository catalogMongoRepository, CatalogSearchService catalogSearchService, CatalogMapper catalogMapper) {
        this.catalogMongoRepository = catalogMongoRepository;
        this.catalogSearchService = catalogSearchService;
        this.catalogMapper = catalogMapper;
    }

    @Transactional
    @TrackPerformanceMetric
    public void createOrUpdateCatalog(BookDto bookDto) {
        BookSummaryDto bookSummaryDto = catalogMapper.bookDtoToBookSummaryDto(bookDto);

        Catalog catalog = catalogMongoRepository
                .findByBookBookIdOrBookIsbn(bookSummaryDto.getBookId(), bookSummaryDto.getIsbn())
                .map(existingCatalog -> { // update book
                    existingCatalog.setBook(bookSummaryDto);
                    return catalogMongoRepository.save(existingCatalog);
                })
                // if there is a book in book-service but no catalog yet
                .orElseGet(() -> { // create new catalog
                    Catalog newCatalog = new Catalog(bookSummaryDto, List.of()); // immutable list directly save to DB
                    return catalogMongoRepository.save(newCatalog);
                });

        // each created or updated catalog should be sync to elasticsearch
        catalogSearchService.saveCatalogIndex(catalog);

        CatalogDto catalogDto = catalogMapper.toDto(catalog);

        log.info("createOrUpdateCatalog in catalog: {}", catalogDto);

    }

    @Transactional
    public CatalogDto addLibraryToBook(BookSummaryDto bookSummaryDto, LibrarySummaryDto librarySummaryDto) {
        Catalog catalog = catalogMongoRepository
                .findByBookBookIdOrBookIsbn(bookSummaryDto.getBookId(), bookSummaryDto.getIsbn())
                .map(existingCatalog -> updateLibraries(existingCatalog, librarySummaryDto))
                .orElseGet(() -> {
                    // if there is a book with libraries in library-service but no catalog yet
                    // create new catalog and update libraries then save
                    Catalog newCatalog = new Catalog(bookSummaryDto, new ArrayList<>()); // mutable list if there will be any change after
                    return updateLibraries(newCatalog, librarySummaryDto);
                });

        // each created or updated catalog should be sync to elasticsearch
        catalogSearchService.saveCatalogIndex(catalog);

        return catalogMapper.toDto(catalog);
    }

    @TrackPerformanceMetric
    public void mapLibraryDtoToSummaryDtos(LibraryDto libraryDto) {
        LibrarySummaryDto librarySummaryDto = catalogMapper.libraryDtoToLibrarySummaryDto(libraryDto);

        List<BookDto> books = libraryDto.getBooks();
        if (books != null && !books.isEmpty()) {
            books.stream()
                    .map(catalogMapper::bookDtoToBookSummaryDto)
                    .map(bookSummaryDto -> addLibraryToBook(bookSummaryDto, librarySummaryDto))
                    .forEach(catalogDto -> log.info("addLibraryToBook in catalog: {}", catalogDto));
        }
    }

    private Catalog updateLibraries(Catalog catalog, LibrarySummaryDto librarySummaryDto) {
        List<LibrarySummaryDto> libraries = catalog.getLibraries();
        if (libraries == null) {
            libraries = new ArrayList<>();
            catalog.setLibraries(libraries);
        }

        boolean exists = libraries.stream()
                .anyMatch(lib -> lib.getLibraryId().equals(librarySummaryDto.getLibraryId()));

        if (!exists) {
            libraries.add(librarySummaryDto);
            catalog = catalogMongoRepository.save(catalog);
        }

        return catalog;
    }
}
