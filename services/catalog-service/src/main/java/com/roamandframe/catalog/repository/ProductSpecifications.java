package com.roamandframe.catalog.repository;

import org.springframework.data.jpa.domain.Specification;

final class ProductSpecifications {

    private ProductSpecifications() {
    }

    static Specification<ProductJpaEntity> matching(String categoryCode, String brand) {
        return (root, query, cb) -> {
            var active = cb.isTrue(root.get("active"));
            var predicate = active;
            if (hasText(categoryCode)) {
                predicate = cb.and(predicate, cb.equal(root.get("category").get("code"), categoryCode));
            }
            if (hasText(brand)) {
                predicate = cb.and(predicate, cb.equal(root.get("brand"), brand));
            }
            return predicate;
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
