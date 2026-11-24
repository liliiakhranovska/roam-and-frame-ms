package com.roamandframe.order.repository;

import com.roamandframe.order.model.OrderFilter;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


final class OrderSpecifications {

    private OrderSpecifications() {
    }

    static Specification<OrderJpaEntity> matching(OrderFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.customerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), filter.customerId()));
            }
            if (filter.statuses() != null && !filter.statuses().isEmpty()) {
                predicates.add(root.get("status").in(filter.statuses()));
            }
            if (filter.createdFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<Instant>get("createdAt"), filter.createdFrom()));
            }
            if (filter.createdBefore() != null) {
                predicates.add(cb.lessThan(root.<Instant>get("createdAt"), filter.createdBefore()));
            }
            if (filter.shippedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<Instant>get("shippedAt"), filter.shippedFrom()));
            }
            if (filter.shippedBefore() != null) {
                predicates.add(cb.lessThan(root.<Instant>get("shippedAt"), filter.shippedBefore()));
            }
            if (filter.minTotal() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("total"), filter.minTotal()));
            }
            if (filter.maxTotal() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("total"), filter.maxTotal()));
            }
            if (filter.sku() != null) {
                // exists-subquery instead of a join: no duplicate rows, and it works with the id-first query
                Subquery<Integer> sub = query.subquery(Integer.class);
                Root<OrderItemJpaEntity> item = sub.from(OrderItemJpaEntity.class);
                sub.select(cb.literal(1))
                        .where(cb.equal(item.get("order"), root), cb.equal(item.get("sku"), filter.sku()));
                predicates.add(cb.exists(sub));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

