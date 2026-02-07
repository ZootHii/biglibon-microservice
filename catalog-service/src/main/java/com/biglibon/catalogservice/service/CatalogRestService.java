package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.model.CatalogIndex;
import com.biglibon.catalogservice.repository.CatalogMongoRepository;
import com.biglibon.sharedlibrary.dto.CatalogDto;
import com.biglibon.sharedlibrary.performance.TrackPerformanceMetric;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
    public List<CatalogDto> findAll() {
        return catalogMapper.toDtoList(catalogMongoRepository.findAll());
    }

    @TrackPerformanceMetric
    public Iterable<CatalogIndex> findAllCatalogIndex() {
        return catalogSearchService.findAll();
    }

    // catalog a map etmenin bi anlamı olmayabilir iptal ederiz bunu
    @TrackPerformanceMetric
    public List<Catalog> search(String text) throws IOException {
        return catalogMapper.indexToCatalogList(catalogSearchService.searchByText(text));
    }
}
