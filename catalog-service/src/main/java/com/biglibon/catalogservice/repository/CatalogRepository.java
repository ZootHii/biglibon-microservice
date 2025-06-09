package com.biglibon.catalogservice.repository;

import com.biglibon.catalogservice.model.Catalog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CatalogRepository extends MongoRepository<Catalog, String> {
}
