package com.biglibon.catalogservice.repository;

import com.biglibon.catalogservice.model.CatalogIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CatalogElasticsearchRepository extends ElasticsearchRepository<CatalogIndex, String> {
}
