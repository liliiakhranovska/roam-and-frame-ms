package com.roamandframe.shipping.service;

import com.roamandframe.shipping.exception.ShipmentAlreadyShippedException;
import com.roamandframe.shipping.exception.ShipmentCancelledException;
import com.roamandframe.shipping.exception.ShipmentNotFoundException;
import com.roamandframe.shipping.gateway.ShippingGateway;
import com.roamandframe.shipping.model.DeliveryAddress;
import com.roamandframe.shipping.model.Shipment;
import com.roamandframe.shipping.model.ShipmentStatus;
import com.roamandframe.shipping.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class ShippingServiceImpl implements ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final ShippingGateway shippingGateway;

    public ShippingServiceImpl(ShipmentRepository shipmentRepository, ShippingGateway shippingGateway) {
        this.shipmentRepository = shipmentRepository;
        this.shippingGateway = shippingGateway;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<UUID, Shipment> findByOrderIds(Collection<UUID> orderIds) {
        return shipmentRepository.findByOrderIds(orderIds).stream()
                .collect(Collectors.toMap(Shipment::orderId, Function.identity()));
    }

    @Transactional
    @Override
    public Shipment markShipped(UUID orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(orderId));
        if (shipment.status() == ShipmentStatus.CANCELLED) {
            throw new ShipmentCancelledException(orderId);
        }
        shipmentRepository.markShipped(orderId);
        return new Shipment(shipment.id(), orderId, shipment.customerId(), ShipmentStatus.SHIPPED,
                shipment.trackingNumber());
    }

    @Transactional
    @Override
    public Shipment create(UUID orderId, UUID customerId, DeliveryAddress address) {
        Optional<Shipment> existing = shipmentRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            if (existing.get().status() == ShipmentStatus.CANCELLED) {
                throw new ShipmentCancelledException(orderId);
            }
            return existing.get(); // a retry of a call that already succeeded
        }
        String trackingNumber = shippingGateway.createShipment(orderId, address);
        try {
            return shipmentRepository.create(orderId, customerId, trackingNumber);
        } catch (RuntimeException e) {
            shippingGateway.cancelShipment(trackingNumber);
            throw e;
        }
    }

    @Transactional
    @Override
    public void cancel(UUID orderId) {
        shipmentRepository.findByOrderId(orderId).ifPresent(shipment -> {
            if (shipment.status() == ShipmentStatus.SHIPPED) {
                throw new ShipmentAlreadyShippedException(orderId);
            }
            if (shipment.status() == ShipmentStatus.INITIATED) {
                shippingGateway.cancelShipment(shipment.trackingNumber());
                shipmentRepository.markCancelled(orderId);
            }
        });
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}

