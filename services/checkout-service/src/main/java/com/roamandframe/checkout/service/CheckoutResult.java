package com.roamandframe.checkout.service;

import com.roamandframe.checkout.client.PlacedOrder;
import com.roamandframe.checkout.client.ShipmentResult;

public record CheckoutResult(PlacedOrder order, ShipmentResult shipment) {
}

