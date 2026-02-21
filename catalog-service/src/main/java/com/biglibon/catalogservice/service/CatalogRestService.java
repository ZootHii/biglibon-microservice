package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.model.CatalogIndex;
import com.biglibon.catalogservice.repository.CatalogMongoRepository;
import com.biglibon.sharedlibrary.dto.CatalogDto;
import com.biglibon.sharedlibrary.performance.TrackPerformanceMetric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CatalogRestService {

    private final CatalogMongoRepository catalogMongoRepository;
    private final CatalogSearchService catalogSearchService;
    private final CatalogMapper catalogMapper;

    public CatalogRestService(CatalogMongoRepository catalogMongoRepository, CatalogSearchService catalogSearchService, CatalogMapper catalogMapper) {
        this.catalogMongoRepository = catalogMongoRepository;
        this.catalogSearchService = catalogSearchService;
        this.catalogMapper = catalogMapper;
    }

    @TrackPerformanceMetric
    @Cacheable(
            value = "catalog-mongo-cache",   // özel tanım yapmadığın herhangi bir isim
            key = "'all'",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<CatalogDto> findAll() {
        log.info("Cache MISS - CatalogRestService - findAll");
        return catalogMapper.toDtoList(catalogMongoRepository.findAll());
    }

    @TrackPerformanceMetric
    @Cacheable(
            value = "catalog-elasticsearch-cache",   // özel tanım yapmadığın herhangi bir isim
            key = "'all'",
            unless = "#result == null || #result.isEmpty()"
    )
    public Iterable<CatalogIndex> findAllCatalogIndex() {
        log.info("Cache MISS - CatalogRestService - findAllCatalogIndex");
        return catalogSearchService.findAll();
    }

    // catalog a map etmenin bi anlamı olmayabilir iptal ederiz bunu
    @TrackPerformanceMetric
    public List<Catalog> search(String text) throws IOException {
        return catalogMapper.indexToCatalogList(catalogSearchService.searchByText(text));
    }
}
