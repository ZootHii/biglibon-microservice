package com.biglibon.catalogservice.controller;


import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/catalogs")
@Validated
public class CatalogController {

    Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;
    private final Environment environment;

    public CatalogController(CatalogService catalogService, Environment environment) {
        this.catalogService = catalogService;
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<List<Catalog>> getAll() {
        logger.info("Catalog Service port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(catalogService.findAll());
    }

}
