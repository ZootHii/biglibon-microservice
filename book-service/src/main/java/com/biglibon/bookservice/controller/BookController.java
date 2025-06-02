//package com.biglibon.bookservice.controller;
//
//import com.biglibon.bookservice.dto.BookDto;
//import com.biglibon.bookservice.service.BookService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//import java.util.List;
//
//@RestController
//@RequestMapping("/v1/books")
//@Validated
//public class BookController {
//
//    Logger logger = LoggerFactory.getLogger(BookController.class);
//
//    private final BookService service;
//
//    public BookController(BookService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public ResponseEntity<BookDto> create(@RequestBody BookDto bookDto) {
//        return ResponseEntity.ok(service.create(bookDto));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<BookDto>> getAll() {
//        return ResponseEntity.ok(service.findAll());
//    }
//
//    @GetMapping("/by-ids/{ids}")
//    public ResponseEntity<List<BookDto>> getAllByIds(@RequestParam List<Long> ids) {
//        return ResponseEntity.ok(service.findAllByIds(ids));
//    }
//
//    @GetMapping("/isbn/{isbn}")
//    public ResponseEntity<BookDto> getByIsbn(@PathVariable @NotEmpty String isbn) {
//        logger.info("Book requested by isbn: {}", isbn);
//        return ResponseEntity.ok(service.findByIsbn(isbn));
//    }
//
//    @GetMapping("/id/{id}")
//    public ResponseEntity<BookDto> getById(@PathVariable @NotNull Long id) {
//        return ResponseEntity.ok(service.findById(id));
//    }
//}
