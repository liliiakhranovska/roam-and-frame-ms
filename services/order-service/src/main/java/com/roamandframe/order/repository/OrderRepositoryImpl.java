package com.roamandframe.order.repository;

import com.roamandframe.order.model.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private static final int MAX_RESULTS = 500;

    OrderRepositoryImpl(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order create(UUID id, UUID customerId, List<OrderItem> items, BigDecimal total) {
        return jpaRepository.saveAndFlush(new OrderJpaEntity(id, customerId, total, items)).toModel();
    }


    @Transactional(readOnly = true)
    @Override
    public List<Order> search(OrderFilter filter) {
        PageRequest page = PageRequest.of(0, MAX_RESULTS, toSort(filter));

        List<UUID> ids = jpaRepository.findAll(OrderSpecifications.matching(filter), page)
                .getContent().stream()
                .map(OrderJpaEntity::getId)
                .toList();
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<UUID, OrderJpaEntity> byId = jpaRepository.findByIdIn(ids).stream()
                .collect(Collectors.toMap(OrderJpaEntity::getId, Function.identity()));
        return ids.stream().map(byId::get).map(OrderJpaEntity::toModel).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderJpaEntity::toModel);
    }

    @Transactional
    @Override
    public void markShipped(UUID id) {
        jpaRepository.findById(id).ifPresent(OrderJpaEntity::ship);
    }

    @Transactional
    @Override
    public void markCancelled(UUID id) {
        jpaRepository.findById(id).ifPresent(OrderJpaEntity::cancel);
    }

    private static Sort toSort(OrderFilter filter) {
        OrderSort sort = filter.sort() == null ? OrderSort.CREATED_AT : filter.sort();
        String property = switch (sort) {
            case CREATED_AT -> "createdAt";
            case TOTAL -> "total";
        };
        Sort.Direction direction = filter.descending() ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, property).and(Sort.by("id"));   // id = stable tiebreaker
    }
}

