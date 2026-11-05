package com.roamandframe.checkout.client;

import java.math.BigDecimal;
import java.util.List;

public record CartContents(List<CartLine> items, BigDecimal total) {
}
