package com.biglibon.bookservice.repository;

import com.biglibon.bookservice.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn);

    Optional<List<Book>> findAllByIsbnIn(List<String> isbns);
}
