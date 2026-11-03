package com.roamandframe.catalog.repository;

import com.roamandframe.catalog.model.Product;
import com.roamandframe.catalog.model.ProductSummary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
class  CatalogRepositoryImpl implements CatalogRepository {

    private final ProductJpaRepository productJpaRepository;

    CatalogRepositoryImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<ProductSummary> search(String categoryCode, String brand) {
        return productJpaRepository
                .findAll(ProductSpecifications.matching(categoryCode, brand), Sort.by("name"))
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productJpaRepository.findBySku(sku).map(this::toProduct);
    }

    @Override
    public List<Product> findBySkus(Collection<String> skus) {
        return productJpaRepository.findBySkuIn(skus).stream().map(this::toProduct).toList();
    }

    private ProductSummary toSummary(ProductJpaEntity e) {
        return new ProductSummary(e.getId(), e.getSku(), e.getName(), e.getBrand(),
                e.getPrice(), e.getCategory().getCode());
    }

    private Product toProduct(ProductJpaEntity e) {
        return new Product(e.getId(), e.getSku(), e.getName(), e.getDescription(), e.getBrand(),
                e.getPrice(), e.getCategory().getCode(), e.getCategory().getName(),
                e.getAttributes());
    }
}
