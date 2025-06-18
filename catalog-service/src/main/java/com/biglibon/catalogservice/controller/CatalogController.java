package com.biglibon.catalogservice.controller;


import com.biglibon.catalogservice.service.CatalogService;
import com.biglibon.sharedlibrary.dto.CatalogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/v1/catalogs")
@RestController
@Validated
@Slf4j
public class CatalogController {

    private final CatalogService catalogService;
    private final Environment environment;

    public CatalogController(CatalogService catalogService, Environment environment) {
        this.catalogService = catalogService;
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<List<CatalogDto>> getAll() {
        log.info("Catalog getAll port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(catalogService.findAll());
    }
}
