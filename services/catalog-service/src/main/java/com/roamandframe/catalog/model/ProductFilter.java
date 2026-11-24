package com.roamandframe.catalog.model;

import java.math.BigDecimal;
import java.util.Set;

public record ProductFilter(
        Set<String> categories, Set<String> brands, Set<String> skus,
        String text, BigDecimal minPrice, BigDecimal maxPrice,
        Boolean waterproof, Boolean weatherSealed, String gender, Integer maxWeightG,
        Boolean inStock,
        ProductSort sort, boolean descending) { }
