package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.repository.CatalogRepository;
import com.biglibon.sharedlibrary.dto.BookSummaryDto;
import com.biglibon.sharedlibrary.dto.CatalogDto;
import com.biglibon.sharedlibrary.dto.LibrarySummaryDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;

    public CatalogService(CatalogRepository catalogRepository, CatalogMapper catalogMapper) {
        this.catalogRepository = catalogRepository;
        this.catalogMapper = catalogMapper;
    }

    public List<CatalogDto> findAll() {
        return catalogMapper.toDtoList(catalogRepository.findAll());
    }


    public CatalogDto addOrUpdateBook(BookSummaryDto bookSummaryDto) {
        Catalog createdOrUpdatedCatalog = catalogRepository
                .findByBookBookIdOrBookIsbn(bookSummaryDto.getBookId(), bookSummaryDto.getIsbn())
                .map(existingCatalog -> {
                    existingCatalog.setBook(bookSummaryDto);
                    return catalogRepository.save(existingCatalog);
                })
                .orElseGet(() -> { // if there is a book in book-service but no catalog yet
                    Catalog newCatalog = new Catalog(bookSummaryDto, new ArrayList<>());
                    return catalogRepository.save(newCatalog);
                });

        return catalogMapper.toDto(createdOrUpdatedCatalog);
    }

    public CatalogDto addLibraryToBook(BookSummaryDto bookSummaryDto, LibrarySummaryDto librarySummaryDto) {
        Catalog createdOrUpdatedCatalog = catalogRepository
                .findByBookBookIdOrBookIsbn(bookSummaryDto.getBookId(), bookSummaryDto.getIsbn())
                .map(existingCatalog -> {
                    List<LibrarySummaryDto> libraries = existingCatalog.getLibraries();
                    if (libraries == null) libraries = new ArrayList<>(); // imkansız sanırım empty list atıyoruz direkt

                    boolean libraryExists = libraries
                            .stream()
                            .anyMatch(existingLibrary ->
                                    existingLibrary.getLibraryId().equals(librarySummaryDto.getLibraryId()));

                    if (!libraryExists) {
                        libraries.add(librarySummaryDto);
                    }

                    existingCatalog.setLibraries(libraries);
                    return catalogRepository.save(existingCatalog);
                })
                .orElseGet(() -> { // if there is a book with libraries in library-service but no catalog yet
                    Catalog newCatalog = new Catalog(bookSummaryDto, new ArrayList<>());
                    List<LibrarySummaryDto> libraries = newCatalog.getLibraries();

                    boolean libraryExists = libraries
                            .stream()
                            .anyMatch(existingLibrary ->
                                    existingLibrary.getLibraryId().equals(librarySummaryDto.getLibraryId()));

                    if (!libraryExists) {
                        libraries.add(librarySummaryDto);
                    }

                    newCatalog.setLibraries(libraries);
                    return catalogRepository.save(newCatalog);
                });
        return catalogMapper.toDto(createdOrUpdatedCatalog);
    }
}
