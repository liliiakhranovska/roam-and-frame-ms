package com.roamandframe.cart.service;

import com.roamandframe.cart.client.CatalogClient;
import com.roamandframe.cart.client.CatalogProduct;
import com.roamandframe.cart.exception.EmptyCartException;
import com.roamandframe.cart.exception.InsufficientStockException;
import com.roamandframe.cart.exception.InvalidCartException;
import com.roamandframe.cart.exception.ProductNotFoundException;
import com.roamandframe.cart.model.Cart;
import com.roamandframe.cart.model.CartItem;
import com.roamandframe.cart.model.CartLine;
import com.roamandframe.cart.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CatalogClient catalogClient;

    public CartServiceImpl(CartRepository cartRepository, CatalogClient catalogClient) {
        this.cartRepository = cartRepository;
        this.catalogClient = catalogClient;
    }

    @Override
    public Cart getCart(UUID customerId) {
        return buildCart(customerId);
    }

    @Override
    public Cart setItemQuantity(UUID customerId, String sku, int quantity) {
        if (quantity == 0) {
            cartRepository.deleteItem(customerId, sku);
        } else {
            CatalogProduct product = catalogClient.getProducts(List.of(sku)).get(sku);
            if (product == null) {
                throw new ProductNotFoundException(sku);
            }
            if (quantity > product.quantity()) {
                throw new InsufficientStockException(sku, quantity, product.quantity());
            }
            cartRepository.saveItem(customerId, sku, quantity);
        }
        return buildCart(customerId);
    }

    @Override
    public Cart validateCart(UUID customerId) {
        List<CartItem> items = cartRepository.findItems(customerId);
        if (items.isEmpty()) {
            throw new EmptyCartException();
        }
        Cart cart = toCart(items);
        List<String> problems = cart.lines().stream().map(this::problemOf).filter(Objects::nonNull).toList();
        if (!problems.isEmpty()) {
            throw new InvalidCartException(problems);
        }
        return cart;
    }

    @Override
    public void clearCart(UUID customerId) {
        cartRepository.deleteAll(customerId);
    }

    private Cart buildCart(UUID customerId) {
        return toCart(cartRepository.findItems(customerId));
    }

    private Cart toCart(List<CartItem> items) {
        Map<String, CatalogProduct> products = items.isEmpty()
                ? Map.of()
                : catalogClient.getProducts((items.stream().map(CartItem::sku).toList()));

        List<CartLine> lines = items.stream().map(i -> toLine(i, products.get(i.sku()))).toList();
        BigDecimal total = lines.stream()
                .map(CartLine::lineTotal)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
        return new Cart(lines, total);
    }

    private String problemOf(CartLine line) {
        if (line.name() == null) {
            return line.sku() + ": product is no longer available";
        }
        if (line.quantity() > line.availableQuantity()) {
            return line.sku() + ": requested " + line.quantity() + ", available " + line.availableQuantity();
        }
        return null;
    }

    private CartLine toLine(CartItem item, CatalogProduct product) {
        if (product == null) {
            return new CartLine(item.sku(), null, null, item.quantity(), null, 0);
        }
        BigDecimal unitPrice = product.price();
        return new CartLine(item.sku(), product.name(), unitPrice, item.quantity(),
                unitPrice.multiply(BigDecimal.valueOf(item.quantity())), product.quantity());
    }
}

