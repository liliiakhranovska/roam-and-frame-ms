package com.roamandframe.catalog.repository;

import com.roamandframe.catalog.model.Category;
import com.roamandframe.catalog.model.PriceRange;
import com.roamandframe.catalog.model.Product;
import com.roamandframe.catalog.model.ProductFilter;
import com.roamandframe.catalog.model.ProductSummary;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    List<ProductSummary> search(ProductFilter filter);
    Optional<Product> findBySku(String sku);
    List<Product> findBySkus(Collection<String> skus);
    List<Category> findCategories();
    List<String> findBrands();
    PriceRange findPriceRange();
}
