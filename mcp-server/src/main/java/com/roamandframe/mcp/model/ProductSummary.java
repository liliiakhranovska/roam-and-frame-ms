package com.roamandframe.mcp.model;

import java.math.BigDecimal;

public record ProductSummary(String sku, String name, String brand, BigDecimal price,
                             String categoryCode, int quantity) { }
