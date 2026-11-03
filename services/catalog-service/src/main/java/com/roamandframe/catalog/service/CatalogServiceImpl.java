package com.roamandframe.catalog.service;

import com.roamandframe.catalog.client.InventoryClient;
import com.roamandframe.catalog.exception.ProductNotFoundException;
import com.roamandframe.catalog.model.Product;
import com.roamandframe.catalog.model.ProductSummary;
import com.roamandframe.catalog.model.WithStock;
import com.roamandframe.catalog.repository.CatalogRepository;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;
    private final InventoryClient inventoryClient;

    public CatalogServiceImpl(CatalogRepository catalogRepository, InventoryClient inventoryClient) {
        this.catalogRepository = catalogRepository;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public List<WithStock<ProductSummary>> searchProducts(String categoryCode, String brand, Boolean inStock) {
        List<ProductSummary> products = catalogRepository.search(categoryCode, brand);
        Map<String, Integer> quantities = inventoryClient.getQuantities(
                products.stream().map(ProductSummary::sku).toList());
        return products.stream()
                .map(p -> new WithStock<>(p, quantities.getOrDefault(p.sku(), 0)))
                .filter(p -> inStock == null || (p.quantity() > 0) == inStock)
                .toList();
    }

    @Override
    public Map<String, WithStock<Product>> getProducts(Collection<String> skus) {
        List<Product> products = catalogRepository.findBySkus(skus);
        Map<String, Integer> quantities = inventoryClient.getQuantities(
                products.stream().map(Product::sku).toList());
        return products.stream().collect(Collectors.toMap(
                Product::sku, p -> new WithStock<>(p, quantities.getOrDefault(p.sku(), 0))));
    }

    @Override
    public WithStock<Product> getProduct(String sku) {
        Product product = catalogRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));
        return new WithStock<>(product, inventoryClient.getQuantities(List.of(sku)).get(sku));
    }
}

