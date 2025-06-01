package com.biglibon.libraryservice.client;

import com.biglibon.libraryservice.dto.BookDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "book-service", path = "/v1/books")
public interface BookServiceClient {

    Logger logger = LoggerFactory.getLogger(BookServiceClient.class);

    @GetMapping("/isbn/{isbn}") // this will work with circuitBreaker before errorDecoder
    @CircuitBreaker(name = "getByIsbnCircuitBreaker", fallbackMethod = "getByIsbnFallback")
    ResponseEntity<BookDto> getByIsbn(@PathVariable String isbn);

    default ResponseEntity<BookDto> getByIsbnFallback(String isbn, Exception exception) {
        logger.info("Book not found by isbn: " + isbn + ", returning default BookDto object.");
        return ResponseEntity.ok(null);
    }

    @GetMapping("/by-ids/{ids}") // this will work with circuitBreaker before errorDecoder
    @CircuitBreaker(name = "getAllByIdsCircuitBreaker", fallbackMethod = "getAllByIdsFallback")
    ResponseEntity<List<BookDto>> getAllByIds(@RequestParam List<Long> ids);

    default ResponseEntity<List<BookDto>> getAllByIdsFallback(List<Long> ids, Exception exception) {
        logger.info("Books not found by ids: " + ids + ", returning default BookDto object.");
        return ResponseEntity.ok(null);
    }

//    @GetMapping("/id/{id}") // this will work with error decoder there is no fallback/circuitBreaker
//    ResponseEntity<BookDto> getById(@PathVariable String id);

    @GetMapping("/id/{id}")
    @CircuitBreaker(name = "getByIdCircuitBreaker", fallbackMethod = "getByIdFallback")
    ResponseEntity<BookDto> getById(@PathVariable Long id);

    default ResponseEntity<BookDto> getByIdFallback(String id, Exception exception) {
        logger.info("Book not found by id: " + id + ", returning default BookDto object.");
        return ResponseEntity.ok(null);
    }
}
