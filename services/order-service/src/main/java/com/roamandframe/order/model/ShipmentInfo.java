package com.roamandframe.order.model;

import java.util.UUID;

public record ShipmentInfo(UUID orderId, String status, String trackingNumber) { }
