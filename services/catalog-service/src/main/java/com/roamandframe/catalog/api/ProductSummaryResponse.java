package com.roamandframe.catalog.api;


import com.roamandframe.catalog.model.ProductSummary;
import com.roamandframe.catalog.model.WithStock;

import java.math.BigDecimal;

record ProductSummaryResponse(
        String sku, String name, String brand, BigDecimal price, String categoryCode, int quantity
) {
    public static ProductSummaryResponse from(WithStock<ProductSummary> s) {
        ProductSummary p = s.item();
        return new ProductSummaryResponse(p.sku(), p.name(), p.brand(), p.price(), p.categoryCode(),
                s.quantity());
    }
}
