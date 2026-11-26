package com.roamandframe.mcp.model;

import java.math.BigDecimal;
import java.util.Map;

public record ProductDetail(String sku, String name, String description, String brand, BigDecimal price,
                            String categoryCode, String categoryName,
                            Map<String, Object> attributes, Integer quantity) { }
