package com.roamandframe.order.repository;

import com.roamandframe.order.model.OrderFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

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
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

