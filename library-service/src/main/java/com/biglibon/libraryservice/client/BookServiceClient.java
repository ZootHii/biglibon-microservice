package com.biglibon.libraryservice.client;

import com.biglibon.libraryservice.dto.BookDto;
import com.biglibon.libraryservice.dto.BookIdDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "book-service", path = "/v1/books")
public interface BookServiceClient {

    Logger logger = LoggerFactory.getLogger(BookServiceClient.class);

    @GetMapping("/isbn/{isbn}") // this will work with circuitBreaker before errorDecoder
    @CircuitBreaker(name = "getByIsbnCircuitBreaker", fallbackMethod = "getByIsbnFallback")
    ResponseEntity<BookIdDto> getByIsbn(@PathVariable String isbn);

    default ResponseEntity<BookIdDto> getByIsbnFallback(String isbn, Exception exception) {
        logger.info("Book not found by isbn: " + isbn + ", returning default BookDto object.");
        return ResponseEntity.ok(new BookIdDto("default-id", "default-isbn"));
    }

//    @GetMapping("/id/{id}") // this will work with error decoder there is no fallback/circuitBreaker
//    ResponseEntity<BookDto> getById(@PathVariable String id);

    @GetMapping("/id/{id}")
    @CircuitBreaker(name = "getByIdCircuitBreaker", fallbackMethod = "getByIdFallback")
    ResponseEntity<BookDto> getById(@PathVariable String id);

    default ResponseEntity<BookDto> getByIdFallback(String isbn, Exception exception) {
        logger.info("Book not found by id: " + isbn + ", returning default BookDto object.");
        return ResponseEntity.ok(new BookDto(new BookIdDto("default-id", "default-isbn")));
    }
}
