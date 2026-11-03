package com.roamandframe.catalog.api;

import com.roamandframe.catalog.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/catalog")
class InternalCatalogController {

    private final CatalogService catalogService;

    InternalCatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products")
    List<ProductResponse> products(@RequestParam List<String> skus) {
        return catalogService.getProducts(skus).values().stream()
                .map(ProductResponse::from)
                .toList();
    }
}
