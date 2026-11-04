package com.roamandframe.cart.client;

import java.math.BigDecimal;

public record CatalogProduct(String sku, String name, BigDecimal price, int quantity) { }
