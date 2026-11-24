package com.roamandframe.catalog.api;

import com.roamandframe.catalog.model.ProductFilter;
import com.roamandframe.catalog.model.ProductSort;
import com.roamandframe.catalog.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
class CatalogController {

    private final CatalogService catalogService;

    private static final List<String> FILTERABLE_ATTRIBUTES =
            List.of("waterproof", "weatherSealed", "gender", "maxWeightG");

    CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    List<ProductSummaryResponse> search(@RequestParam(required = false) Set<String> category,
                                        @RequestParam(required = false) Set<String> brand,
                                        @RequestParam(required = false) Set<String> sku,
                                        @RequestParam(required = false) String q,
                                        @RequestParam(required = false) BigDecimal minPrice,
                                        @RequestParam(required = false) BigDecimal maxPrice,
                                        @RequestParam(required = false) Boolean waterproof,
                                        @RequestParam(required = false) Boolean weatherSealed,
                                        @RequestParam(required = false) String gender,
                                        @RequestParam(required = false) Integer maxWeightG,
                                        @RequestParam(required = false) Boolean inStock,
                                        @RequestParam(defaultValue = "name") String sort,
                                        @RequestParam(defaultValue = "asc") String direction) {
        if (minPrice != null && minPrice.signum() < 0) throw bad("minPrice must not be negative");
        if (maxPrice != null && maxPrice.signum() < 0) throw bad("maxPrice must not be negative");
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw bad("minPrice must not be greater than maxPrice");
        }
        if (maxWeightG != null && maxWeightG <= 0) throw bad("maxWeightG must be positive");
        if (q != null && q.length() > 100) throw bad("q must be at most 100 characters");

        ProductSort sortBy;
        try {
            sortBy = ProductSort.from(sort);
        } catch (IllegalArgumentException e) {
            throw bad("sort must be one of: name, price, createdAt");
        }
        boolean descending = switch (direction.toLowerCase(Locale.ROOT)) {
            case "asc" -> false;
            case "desc" -> true;
            default -> throw bad("direction must be 'asc' or 'desc'");
        };

        ProductFilter filter = new ProductFilter(clean(category), clean(brand), clean(sku),
                blankToNull(q), minPrice, maxPrice, waterproof, weatherSealed,
                blankToNull(gender), maxWeightG, inStock, sortBy, descending);

        return catalogService.searchProducts(filter).stream()
                .map(ProductSummaryResponse::from)
                .toList();
    }

    @GetMapping("/{sku}")
    ProductResponse getProduct(@PathVariable String sku) {
        return ProductResponse.from(catalogService.getProduct(sku));
    }

    @GetMapping("/filters")
    FacetsResponse filters() {
        return FacetsResponse.of(catalogService.getCategories(), catalogService.getBrands(),
                catalogService.getPriceRange(), FILTERABLE_ATTRIBUTES);
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static Set<String> clean(Set<String> values) {
        if (values == null) return null;
        Set<String> cleaned = values.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());
        return cleaned.isEmpty() ? null : cleaned;
    }
}
