package com.biglibon.catalogservice.controller;


import com.biglibon.catalogservice.model.Catalog;
import com.biglibon.catalogservice.model.CatalogIndex;
import com.biglibon.catalogservice.service.CatalogRestService;
import com.biglibon.sharedlibrary.dto.CatalogDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("/v1/catalogs")
@RestController
@Validated
@Slf4j
public class CatalogController {

    private final CatalogRestService catalogRestService;
    private final Environment environment;

    public CatalogController(CatalogRestService catalogRestService, Environment environment) {
        this.catalogRestService = catalogRestService;
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<List<CatalogDto>> getAll() {
        log.info("Catalog getAll port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(catalogRestService.findAll());
    }

    @GetMapping("/index")
    public ResponseEntity<Iterable<CatalogIndex>> getAllCatalogIndex() {
        return ResponseEntity.ok(catalogRestService.findAllCatalogIndex());
    }

    @GetMapping("/search/{text}")
    public ResponseEntity<List<Catalog>> search(@PathVariable @NotEmpty String text) throws IOException {
        log.info("Catalog search port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok(catalogRestService.search(text));
    }
}
