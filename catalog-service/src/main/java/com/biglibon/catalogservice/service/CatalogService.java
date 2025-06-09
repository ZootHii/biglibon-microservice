package com.biglibon.catalogservice.service;

import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.repository.CatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    // mapper and dto düzeltilecek
    public List<Catalog> findAll() {
        return catalogRepository.findAll();
    }
}
