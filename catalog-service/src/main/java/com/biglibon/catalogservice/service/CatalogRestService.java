package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.mapper.CatalogMapper;
import com.biglibon.catalogservice.model.CatalogIndex;
import com.biglibon.catalogservice.repository.CatalogMongoRepository;
import com.biglibon.sharedlibrary.dto.CatalogDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
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

    public List<CatalogDto> findAll() {
        return catalogMapper.toDtoList(catalogMongoRepository.findAll());
    }

    public Iterable<CatalogIndex> findAllCatalogIndex() {
        return catalogSearchService.findAll();
    }

    public List<CatalogDto> search(String text) throws IOException {
        return catalogMapper.indexToDtoList(catalogSearchService.searchByText(text));
    }
}
