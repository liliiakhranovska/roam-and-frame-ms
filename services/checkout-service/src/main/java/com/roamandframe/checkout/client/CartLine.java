package com.roamandframe.checkout.client;

import java.math.BigDecimal;

public record CartLine(String sku, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {
}

