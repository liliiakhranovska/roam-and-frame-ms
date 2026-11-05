package com.roamandframe.checkout.service;

import com.roamandframe.checkout.client.CartClient;
import com.roamandframe.checkout.client.CartContents;
import com.roamandframe.checkout.client.CustomerClient;
import com.roamandframe.checkout.client.CustomerProfile;
import com.roamandframe.checkout.client.InventoryClient;
import com.roamandframe.checkout.client.NotificationClient;
import com.roamandframe.checkout.client.OrderClient;
import com.roamandframe.checkout.client.PaymentClient;
import com.roamandframe.checkout.client.PaymentResult;
import com.roamandframe.checkout.client.PlacedOrder;
import com.roamandframe.checkout.client.ShipmentResult;
import com.roamandframe.checkout.client.ShippingClient;
import com.roamandframe.checkout.exception.StepRejectedException;
import com.roamandframe.checkout.model.Checkout;
import com.roamandframe.checkout.model.CheckoutStatus;
import com.roamandframe.checkout.repository.CheckoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class CheckoutServiceImpl implements CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    // Saga steps that have an undo (steps 1 and 2 only read)
    private static final int INVENTORY = 3;
    private static final int PAYMENT = 4;
    private static final int ORDER = 5;
    private static final int SHIPPING = 6;

    private final CheckoutRepository repository;
    private final CustomerClient customerClient;
    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderClient orderClient;
    private final ShippingClient shippingClient;
    private final NotificationClient notificationClient;

    CheckoutServiceImpl(CheckoutRepository repository, CustomerClient customerClient, CartClient cartClient,
                        InventoryClient inventoryClient, PaymentClient paymentClient, OrderClient orderClient,
                        ShippingClient shippingClient, NotificationClient notificationClient) {
        this.repository = repository;
        this.customerClient = customerClient;
        this.cartClient = cartClient;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
        this.orderClient = orderClient;
        this.shippingClient = shippingClient;
        this.notificationClient = notificationClient;
    }

    @Override
    public CheckoutResult checkout(UUID customerId) {
        UUID orderId = UUID.randomUUID();
        repository.start(orderId, customerId);

        int lastStep = 0;
        CustomerProfile customer;
        PaymentResult payment;
        PlacedOrder order;
        ShipmentResult shipment;
        try {
            customer = customerClient.getProfile(customerId);
            requireDeliveryAddress(customer);
            repository.recordStep(orderId, lastStep = 1);

            CartContents cart = cartClient.validate(customerId);
            repository.recordStep(orderId, lastStep = 2);

            inventoryClient.reserve(orderId, cart.items());
            repository.recordStep(orderId, lastStep = INVENTORY);

            payment = paymentClient.authorize(orderId, customerId, cart.total());
            repository.recordStep(orderId, lastStep = PAYMENT);

            order = orderClient.create(orderId, customerId, cart.items());
            repository.recordStep(orderId, lastStep = ORDER);

            shipment = shippingClient.create(orderId, customer);
            repository.recordStep(orderId, lastStep = SHIPPING);

            repository.setStatus(orderId, CheckoutStatus.FINISHING, null);
        } catch (RuntimeException e) {
            compensate(orderId, lastStep, e.getMessage());
            throw e;
        }

        // From here the order is complete: nothing is undone any more.
        finishCart(orderId, customerId);
        sendConfirmation(customer, order, payment, shipment);
        return new CheckoutResult(order, shipment);
    }

    @Override
    public void recover(Checkout stale) {
        switch (stale.status()) {
            case RUNNING -> compensate(stale.id(), stale.lastStep(), "Interrupted before completion");
            case COMPENSATING -> compensate(stale.id(), stale.lastStep(), stale.failureReason());
            case FINISHING -> finishCart(stale.id(), stale.customerId());
            default -> { }
        }
    }

    private void requireDeliveryAddress(CustomerProfile customer) {
        if (isBlank(customer.addressLine1()) || isBlank(customer.city())
                || isBlank(customer.postalCode()) || isBlank(customer.countryCode())) {
            throw new StepRejectedException(409, "Please complete your delivery address before checkout");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Undoes the completed steps in reverse order, plus the step that was running when it failed
     * (it may have happened without us hearing back). Every undo is safe to repeat. If one fails the
     * checkout stays COMPENSATING and the recovery job tries again.
     */
    private void compensate(UUID orderId, int lastStep, String reason) {
        try {
            repository.setStatus(orderId, CheckoutStatus.COMPENSATING, reason);
            int through = lastStep + 1;
            if (through >= SHIPPING) {
                shippingClient.cancel(orderId);
            }
            if (through >= ORDER) {
                orderClient.cancel(orderId);
            }
            if (through >= PAYMENT) {
                paymentClient.voidAuthorization(orderId);
            }
            if (through >= INVENTORY) {
                inventoryClient.release(orderId);
            }
            repository.setStatus(orderId, CheckoutStatus.FAILED, reason);
        } catch (RuntimeException e) {
            log.error("Undo of checkout {} not finished, recovery will retry", orderId, e);
        }
    }

    /** Step 7. A failure is not an error for the customer: the order is placed, recovery clears the cart. */
    private void finishCart(UUID orderId, UUID customerId) {
        try {
            cartClient.clear(customerId);
            repository.setStatus(orderId, CheckoutStatus.COMPLETED, null);
        } catch (RuntimeException e) {
            log.warn("Cart of customer {} not cleared for checkout {}, recovery will retry", customerId, orderId, e);
        }
    }

    /** Step 8. One try; a failure is only logged. */
    private void sendConfirmation(CustomerProfile customer, PlacedOrder order,
                                  PaymentResult payment, ShipmentResult shipment) {
        try {
            notificationClient.sendOrderConfirmation(customer, order, payment, shipment);
        } catch (RuntimeException e) {
            log.error("Confirmation for order {} not sent", order.id(), e);
        }
    }
}
