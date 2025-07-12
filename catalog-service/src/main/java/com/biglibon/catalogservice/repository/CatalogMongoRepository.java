package com.biglibon.catalogservice.repository;

import com.biglibon.catalogservice.model.Catalog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CatalogMongoRepository extends MongoRepository<Catalog, String> {

    Optional<Catalog> findByBookBookIdOrBookIsbn(String bookId, String isbn);
}
