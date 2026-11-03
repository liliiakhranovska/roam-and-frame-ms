package com.roamandframe.catalog.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record Product(UUID id, String sku, String name, String description, String brand,
                      BigDecimal price, String categoryCode, String categoryName,
                      Map<String, Object> attributes) {
}
