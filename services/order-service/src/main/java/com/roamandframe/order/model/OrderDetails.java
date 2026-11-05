package com.roamandframe.order.model;


/** An order together with its shipment, which is null if none exists. */
public record OrderDetails(Order order, ShipmentInfo shipment) {
}
