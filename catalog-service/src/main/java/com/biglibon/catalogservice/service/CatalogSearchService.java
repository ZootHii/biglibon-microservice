package com.biglibon.catalogservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.model.CatalogIndex;
import com.biglibon.catalogservice.repository.CatalogElasticsearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// https://www.elastic.co/docs/reference/elasticsearch/clients/java/usage/searching
@Service
@Slf4j
public class CatalogSearchService {

    private final ElasticsearchClient client;
    private final CatalogElasticsearchRepository repository;
    private final CatalogMapper mapper;

    public CatalogSearchService(ElasticsearchClient client, CatalogElasticsearchRepository repository,
                                CatalogMapper mapper) {
        this.client = client;
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<CatalogIndex> searchByText(String text) throws IOException {
        // Multi Match Query // query util. @Utiility Class altına taşınabilir
        Query bookQuery = MultiMatchQuery.of(m -> m
                .fields("book.title", "book.author", "book.publisher")
                .query(text)
                .type(TextQueryType.CrossFields)
        )._toQuery();

        Query libraryNestedQuery = NestedQuery.of(n -> n
                .path("libraries")
                .query(MultiMatchQuery.of(m -> m
                        .fields("libraries.name", "libraries.city")
                        .query(text)
                        .type(TextQueryType.BestFields)
                )._toQuery())
                .scoreMode(ChildScoreMode.Max)
        )._toQuery();

        Query combinedQuery = BoolQuery.of(b -> b
                .should(bookQuery)
                .should(libraryNestedQuery)
                .minimumShouldMatch("1")
        )._toQuery();

        SearchRequest request = SearchRequest.of(s -> s
                .index("catalogs_index")
                .query(combinedQuery)
        );

        SearchResponse<CatalogIndex> response = client.search(request, CatalogIndex.class);

        return response
                .hits()
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public Iterable<CatalogIndex> findAll() {
        return repository.findAll();
    }


    public CatalogIndex saveCatalogIndex(Catalog catalog) {
        log.info("saveCatalogIndex Catalog: {}", catalog);
        CatalogIndex catalogIndex = mapper.catalogToIndex(catalog);
        log.info("saveCatalogIndex CatalogIndex: {}", catalogIndex);
        return repository.save(catalogIndex);
    }

    public void saveCatalogIndices(List<Catalog> catalogs) {
        List<CatalogIndex> catalogIndices = mapper.catalogToIndexList(catalogs);
        repository.saveAll(catalogIndices);
    }
}
