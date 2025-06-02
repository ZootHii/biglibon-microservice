package com.biglibon.bookservice.repository;

import com.biglibon.bookservice.model.BookMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface BookRepositoryMongo extends MongoRepository<BookMongo, String> {

    Optional<BookMongo> findByIsbn(String isbn);
}
