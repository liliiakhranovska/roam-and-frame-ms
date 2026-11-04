package com.roamandframe.cart.model;

import java.math.BigDecimal;
import java.util.List;

public record Cart(List<CartLine> lines, BigDecimal total) {
}
