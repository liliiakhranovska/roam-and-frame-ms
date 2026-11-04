package com.roamandframe.cart.repository;

import com.roamandframe.cart.model.CartItem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
class CartRepositoryImpl implements CartRepository {

    private final CartItemJpaRepository jpaRepository;

    CartRepositoryImpl(CartItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<CartItem> findItems(UUID customerId) {
        return jpaRepository.findByCustomerIdOrderByCreatedAt(customerId).stream()
                .map(this::toItem)
                .toList();
    }

    @Transactional
    @Override
    public void saveItem(UUID customerId, String sku, int quantity) {
        jpaRepository.findByCustomerIdAndSku(customerId, sku).ifPresentOrElse(
                entity -> entity.setQuantity(quantity),
                () -> jpaRepository.save(new CartItemJpaEntity(customerId, sku, quantity)));
    }

    @Transactional
    @Override
    public void deleteItem(UUID customerId, String sku) {
        jpaRepository.deleteByCustomerIdAndSku(customerId, sku);
    }

    @Transactional
    @Override
    public void deleteAll(UUID customerId) {
        jpaRepository.deleteByCustomerId(customerId);
    }

    private CartItem toItem(CartItemJpaEntity e) {
        return new CartItem(e.getSku(), e.getQuantity());
    }
}
