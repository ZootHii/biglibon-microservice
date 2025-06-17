package com.biglibon.bookservice.controller;

import com.biglibon.bookservice.service.BookService;
import com.biglibon.sharedlibrary.dto.BookDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RequestMapping("/v1/books")
@RestController
@Validated
@Slf4j
public class BookController {

    private final BookService service;
    private final Environment environment;

    public BookController(BookService service, Environment environment) {
        this.service = service;
        this.environment = environment;
    }

    @PostMapping
    public ResponseEntity<BookDto> create(@RequestBody BookDto bookDto) {
        log.info("Book create on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(service.create(bookDto));
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAll() {
        log.info("Book getAll on port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<BookDto>> getAllByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok(service.findAllByIds(ids));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDto> getByIsbn(@PathVariable @NotEmpty String isbn) {
        log.info("Book getByIsbn isbn: {}", isbn);
        return ResponseEntity.ok(service.findByIsbn(isbn));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<BookDto> getById(@PathVariable @NotEmpty String id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
