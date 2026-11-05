package com.roamandframe.order.repository;

import com.roamandframe.order.model.Order;
import com.roamandframe.order.model.OrderFilter;
import com.roamandframe.order.model.OrderItem;
import com.roamandframe.order.model.OrderStatus;
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
        PageRequest newestFirst = PageRequest.of(0, MAX_RESULTS,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")));

        List<UUID> ids = jpaRepository.findAll(OrderSpecifications.matching(filter), newestFirst)
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

    @Transactional(readOnly = true)
    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdAndStatusNotOrderByCreatedAtDesc(customerId, OrderStatus.CANCELLED).stream()
                .map(OrderJpaEntity::toModel)
                .toList();
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
}

