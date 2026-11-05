package com.roamandframe.checkout.client;

public interface NotificationClient {

    /** One try. The caller logs a failure and carries on. */
    void sendOrderConfirmation(CustomerProfile customer, PlacedOrder order,
                               PaymentResult payment, ShipmentResult shipment);
}
