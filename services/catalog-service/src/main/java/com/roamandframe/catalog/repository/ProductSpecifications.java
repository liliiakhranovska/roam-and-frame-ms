package com.roamandframe.catalog.repository;

import com.roamandframe.catalog.model.ProductFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

final class ProductSpecifications {

    private ProductSpecifications() {
    }

    static Specification<ProductJpaEntity> matching(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("active")));

            if (isNotEmpty(filter.categories())) {
                predicates.add(root.get("category").get("code").in(filter.categories()));
            }
            if (isNotEmpty(filter.brands())) {
                predicates.add(cb.lower(root.<String>get("brand")).in(lowerCase(filter.brands())));
            }
            if (isNotEmpty(filter.skus())) {
                predicates.add(root.get("sku").in(filter.skus()));
            }
            if (filter.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("price"), filter.minPrice()));
            }
            if (filter.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("price"), filter.maxPrice()));
            }
            if (filter.text() != null && !filter.text().isBlank()) {
                String pattern = "%" + escapeLike(filter.text().trim().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.<String>get("name")), pattern, '\\'),
                        cb.like(cb.lower(root.<String>get("description")), pattern, '\\')));
            }

            // attribute filters (jsonb); false or null means "no filter"
            if (Boolean.TRUE.equals(filter.waterproof())) {
                predicates.add(cb.equal(attribute(root, cb, "waterproof"), "true"));
            }
            if (Boolean.TRUE.equals(filter.weatherSealed())) {
                predicates.add(cb.equal(attribute(root, cb, "weatherSealed"), "true"));
            }
            if (filter.gender() != null && !filter.gender().isBlank()) {
                predicates.add(cb.equal(cb.lower(attribute(root, cb, "gender")),
                        filter.gender().trim().toLowerCase(Locale.ROOT)));
            }
            if (filter.maxWeightG() != null) {
                predicates.add(cb.le(attribute(root, cb, "weight_g").as(Integer.class), filter.maxWeightG()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Expression<String> attribute(Root<ProductJpaEntity> root, CriteriaBuilder cb, String key) {
        return cb.function("jsonb_extract_path_text", String.class,
                root.get("attributes"), cb.literal(key));
    }

    private static Set<String> lowerCase(Set<String> values) {
        return values.stream().map(v -> v.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }

    /** Makes %, _ and \ in user input match literally. Backslash must be replaced first. */
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static boolean isNotEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }
}