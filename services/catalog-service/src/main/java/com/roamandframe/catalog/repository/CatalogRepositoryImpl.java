package com.roamandframe.catalog.repository;

import com.roamandframe.catalog.model.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
class  CatalogRepositoryImpl implements CatalogRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    CatalogRepositoryImpl(ProductJpaRepository productJpaRepository,
                          CategoryJpaRepository categoryJpaRepository) {
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public List<ProductSummary> search(ProductFilter filter) {
        return productJpaRepository
                .findAll(ProductSpecifications.matching(filter), toSort(filter))
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

    @Override
    public List<Category> findCategories() {
        return categoryJpaRepository.findAll(Sort.by("name")).stream()
                .map(c -> new Category(c.getCode(), c.getName()))
                .toList();
    }

    @Override
    public List<String> findBrands() {
        return productJpaRepository.findActiveBrands();
    }

    @Override
    public PriceRange findPriceRange() {
        return new PriceRange(productJpaRepository.findMinActivePrice(),
                productJpaRepository.findMaxActivePrice());
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

    private static Sort toSort(ProductFilter filter) {
        ProductSort sort = filter.sort() == null ? ProductSort.NAME : filter.sort();
        String property = switch (sort) {
            case NAME -> "name";
            case PRICE -> "price";
            case CREATED_AT -> "createdAt";
        };
        Sort.Direction direction = filter.descending() ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, property).and(Sort.by("sku"));   // sku = stable tiebreaker
    }
}
