package com.roamandframe.shipping.gateway;

import com.roamandframe.shipping.model.DeliveryAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Stand-in until a real carrier is integrated: always succeeds with a made-up tracking number. */
@Component
class DummyShippingGateway implements ShippingGateway {

    private static final Logger log = LoggerFactory.getLogger(DummyShippingGateway.class);

    @Override
    public String createShipment(UUID orderId, DeliveryAddress address) {
        String trackingNumber = "TRK-" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        log.info("Dummy shipment created: order={}, city={}, tracking={}", orderId, address.city(), trackingNumber);
        return trackingNumber;
    }

    @Override
    public void cancelShipment(String trackingNumber) {
        log.info("Dummy shipment cancelled: tracking={}", trackingNumber);
    }
}

