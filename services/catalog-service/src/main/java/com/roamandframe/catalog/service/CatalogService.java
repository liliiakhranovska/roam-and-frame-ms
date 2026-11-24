package com.roamandframe.catalog.service;

import com.roamandframe.catalog.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CatalogService {
    List<WithStock<ProductSummary>> searchProducts(ProductFilter filter);

    Map<String, WithStock<Product>> getProducts(Collection<String> skus);

    WithStock<Product> getProduct(String sku);

    List<Category> getCategories();

    List<String> getBrands();

    PriceRange getPriceRange();}
