package com.roamandframe.catalog.service;

import com.roamandframe.catalog.model.Product;
import com.roamandframe.catalog.model.ProductSummary;
import com.roamandframe.catalog.model.WithStock;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CatalogService {
    List<WithStock<ProductSummary>> searchProducts(String categoryCode, String brand, Boolean inStock);

    Map<String, WithStock<Product>> getProducts(Collection<String> skus);

    WithStock<Product> getProduct(String sku);
}
