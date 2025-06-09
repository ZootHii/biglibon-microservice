package com.biglibon.catalogservice.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/catalogs")
@Validated
public class CatalogController {

    Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final Environment environment;

    public CatalogController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<String> getAll() {
        logger.info("Catalog Service port: {}", environment.getProperty("local.server.port"));
        return ResponseEntity.ok("Catalog Service");
    }

}
