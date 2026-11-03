package com.roamandframe.catalog.api;


import com.roamandframe.catalog.model.Product;
import com.roamandframe.catalog.model.WithStock;

import java.math.BigDecimal;
import java.util.Map;

record ProductResponse(
        String sku, String name, String description, String brand, BigDecimal price,
        String categoryCode, String categoryName, Map<String, Object> attributes, int quantity
) {
    public static ProductResponse from(WithStock<Product> s) {
        Product p = s.item();
        return new ProductResponse(p.sku(), p.name(), p.description(), p.brand(), p.price(),
                p.categoryCode(), p.categoryName(), p.attributes(), s.quantity());
    }
}
