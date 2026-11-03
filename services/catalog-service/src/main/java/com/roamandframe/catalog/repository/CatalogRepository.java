package com.roamandframe.catalog.repository;

import com.roamandframe.catalog.model.Product;
import com.roamandframe.catalog.model.ProductSummary;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    List<ProductSummary> search(String categoryCode, String brand);
    Optional<Product> findBySku(String sku);
    List<Product> findBySkus(Collection<String> skus);
}
