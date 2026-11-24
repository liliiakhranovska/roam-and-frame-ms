package com.roamandframe.catalog.api;

import com.roamandframe.catalog.model.Category;
import com.roamandframe.catalog.model.PriceRange;

import java.math.BigDecimal;
import java.util.List;

record FacetsResponse(List<CategoryResponse> categories, List<String> brands,
                      PriceRangeResponse price, List<String> attributes) {

    record CategoryResponse(String code, String name) { }

    record PriceRangeResponse(BigDecimal min, BigDecimal max) { }

    static FacetsResponse of(List<Category> categories, List<String> brands,
                             PriceRange price, List<String> attributes) {
        return new FacetsResponse(
                categories.stream().map(c -> new CategoryResponse(c.code(), c.name())).toList(),
                brands,
                new PriceRangeResponse(price.min(), price.max()),
                attributes);
    }
}
