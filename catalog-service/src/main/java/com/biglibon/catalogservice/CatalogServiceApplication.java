package com.biglibon.catalogservice;

import com.biglibon.catalogservice.service.CatalogDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.biglibon.catalogservice", "com.biglibon.sharedlibrary"})
public class CatalogServiceApplication implements CommandLineRunner {

    private final CatalogDataService catalogDataService;

    public CatalogServiceApplication(CatalogDataService catalogDataService) {
        this.catalogDataService = catalogDataService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        catalogDataService.setData();
    }
}
