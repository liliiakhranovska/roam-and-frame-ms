package com.roamandframe.catalog.api;

import com.roamandframe.catalog.service.CatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
class CatalogController {

    private final CatalogService catalogService;

    CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    List<ProductSummaryResponse> search(@RequestParam(required = false) String category,
                                        @RequestParam(required = false) String brand,
                                        @RequestParam(required = false) Boolean inStock) {
        return catalogService.searchProducts(category, brand, inStock).stream()
                .map(ProductSummaryResponse::from)
                .toList();
    }

    @GetMapping("/{sku}")
    ProductResponse getProduct(@PathVariable String sku) {
        return ProductResponse.from(catalogService.getProduct(sku));
    }
}
