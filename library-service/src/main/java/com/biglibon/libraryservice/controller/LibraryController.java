package com.biglibon.libraryservice.controller;

import com.biglibon.libraryservice.dto.AddBooksToLibraryByIdsRequestDto;
import com.biglibon.libraryservice.dto.LibraryDto;
import com.biglibon.libraryservice.service.LibraryService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/libraries")
@Validated
public class LibraryController {

    Logger logger = LoggerFactory.getLogger(LibraryController.class);

    private final LibraryService libraryService;
    private final Environment environment;

    public LibraryController(LibraryService libraryService, Environment environment) {
        this.libraryService = libraryService;
        this.environment = environment;
    }

    @PostMapping
    public ResponseEntity<LibraryDto> create(@RequestBody LibraryDto libraryDto) {
        logger.info("Library created on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.create(libraryDto));
    }

    @GetMapping
    public ResponseEntity<List<LibraryDto>> getAll() {
        logger.info("Library created on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.findAll());
    }

    @PostMapping("/books/add/by-ids")
    public ResponseEntity<Void> addBookToLibrary(@RequestBody AddBooksToLibraryByIdsRequestDto addBooksToLibraryByIdsRequestDto) {
        libraryService.addBooksToLibraryByIds(addBooksToLibraryByIdsRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryDto> getWithBooksById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(libraryService.findWithBooksById(id));
    }


}
