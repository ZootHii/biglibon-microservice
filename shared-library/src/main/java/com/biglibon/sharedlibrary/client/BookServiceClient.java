package com.biglibon.sharedlibrary.client;


import com.biglibon.sharedlibrary.dto.BookDto;
import com.biglibon.sharedlibrary.exception.FeignErrorDecoder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "book-service", path = "/v1/books", configuration = FeignErrorDecoder.class)
public interface BookServiceClient {

    Logger logger = LoggerFactory.getLogger(BookServiceClient.class);

    @GetMapping("/isbn/{isbn}") // this will work with circuitBreaker before errorDecoder
    @CircuitBreaker(name = "getByIsbnCircuitBreaker", fallbackMethod = "getByIsbnFallback")
    ResponseEntity<BookDto> getByIsbn(@PathVariable String isbn);

    default ResponseEntity<BookDto> getByIsbnFallback(String isbn, Exception exception) {
        logger.info("Book not found by isbn: {}, returning default BookDto object.", isbn);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/by-ids/{ids}") // this will work with circuitBreaker before errorDecoder
    @CircuitBreaker(name = "getAllByIdsCircuitBreaker", fallbackMethod = "getAllByIdsFallback")
    ResponseEntity<List<BookDto>> getAllByIds(@RequestParam List<String> ids);

    default ResponseEntity<List<BookDto>> getAllByIdsFallback(List<String> ids, Exception exception) {
        logger.info("Books not found by ids: {}, returning default BookDto object.", ids);
        return ResponseEntity.ok(null);
    }

    @GetMapping
    @CircuitBreaker(name = "getAll", fallbackMethod = "getAll")
    ResponseEntity<List<BookDto>> getAll();

    default ResponseEntity<List<BookDto>> getAll(Exception exception) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/id/{id}")
    ResponseEntity<BookDto> getById(@PathVariable String id);


//    @GetMapping("/id/{id}") // this will work with error decoder there is no fallback/circuitBreaker
//    ResponseEntity<BookDto> getById(@PathVariable String id);

//    @GetMapping("/id/{id}")
//    @CircuitBreaker(name = "getByIdCircuitBreaker", fallbackMethod = "getByIdFallback")
//    ResponseEntity<BookDto> getById(@PathVariable String id);
//
//    default ResponseEntity<BookDto> getByIdFallback(String id, Exception exception) {
//        logger.info("Book not found by id: {}, returning default BookDto object.", id);
//        return ResponseEntity.ok(null);
//    }
}

