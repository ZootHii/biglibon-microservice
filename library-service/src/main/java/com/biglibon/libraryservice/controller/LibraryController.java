package com.biglibon.libraryservice.controller;

import com.biglibon.libraryservice.dto.AddBookRequest;
import com.biglibon.libraryservice.dto.LibraryDto;
import com.biglibon.libraryservice.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
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

    @GetMapping("/{id}")
    public ResponseEntity<LibraryDto> getAllBooksInLibraryById(@PathVariable @NotEmpty String id) {
        return ResponseEntity.ok(libraryService.findAllBooksInLibraryById(id));
    }

    @PostMapping
    public ResponseEntity<LibraryDto> create() {
        logger.info("Library created on port: " + environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.create());
    }

    @PutMapping
    public ResponseEntity<Void> addBookToLibrary(@RequestBody AddBookRequest addBookRequest) {
        libraryService.addBookToLibrary(addBookRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllLibraryIds() {
        return ResponseEntity.ok(libraryService.findAllLibraryIds());
    }
}
