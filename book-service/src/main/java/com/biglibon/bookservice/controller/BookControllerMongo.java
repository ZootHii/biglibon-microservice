package com.biglibon.bookservice.controller;

import com.biglibon.bookservice.dto.BookDtoMongo;
import com.biglibon.bookservice.service.BookServiceMongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/v1/books")
@Validated
public class BookControllerMongo {

    Logger logger = LoggerFactory.getLogger(BookControllerMongo.class);

    private final BookServiceMongo service;

    public BookControllerMongo(BookServiceMongo service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookDtoMongo> create(@RequestBody BookDtoMongo bookDto) {
        return ResponseEntity.ok(service.create(bookDto));
    }

    @GetMapping
    public ResponseEntity<List<BookDtoMongo>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/by-ids/{ids}")
    public ResponseEntity<List<BookDtoMongo>> getAllByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok(service.findAllByIds(ids));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDtoMongo> getByIsbn(@PathVariable @NotEmpty String isbn) {
        logger.info("Book requested by isbn: {}", isbn);
        return ResponseEntity.ok(service.findByIsbn(isbn));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<BookDtoMongo> getById(@PathVariable @NotEmpty String id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
