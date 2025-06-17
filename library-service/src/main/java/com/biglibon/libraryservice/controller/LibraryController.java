package com.biglibon.libraryservice.controller;

import com.biglibon.libraryservice.dto.AddBooksToLibraryByIdsRequest;
import com.biglibon.sharedlibrary.dto.LibraryDto;
import com.biglibon.libraryservice.service.LibraryService;
import com.biglibon.sharedlibrary.dto.BookDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/libraries")
@RestController
@Validated
@Slf4j
public class LibraryController {

    private final LibraryService libraryService;
    private final Environment environment;

    public LibraryController(LibraryService libraryService, Environment environment) {
        this.libraryService = libraryService;
        this.environment = environment;
    }

    @PostMapping
    public ResponseEntity<LibraryDto> create(@RequestBody LibraryDto libraryDto) {
        log.info("Library create on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.create(libraryDto));
    }

    @GetMapping
    public ResponseEntity<List<LibraryDto>> getAll() {
        log.info("Library getAll on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.findAll());
    }

    @PostMapping("/books/add/by-ids")
    public ResponseEntity<Void> addBookToLibrary(
            @RequestBody AddBooksToLibraryByIdsRequest request) {
        libraryService.addBooksToLibraryByIds(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryDto> getWithBooksById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(libraryService.findWithBooksById(id));
    }

    // TESTING
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooksFromLibraryService() {
        log.info("Library getAllBooksFromLibraryService on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.getAllBooksFromLibraryService());
    }

    // TESTING
    @GetMapping("/books/id/{id}")
    public ResponseEntity<BookDto> getBookByIdFromLibraryService(@PathVariable @NotBlank String id) {
        log.info("Library getBookByIdFromLibraryService on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.getBookByIdFromLibraryService(id));
    }
}
