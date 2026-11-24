package com.roamandframe.catalog.model;

import java.math.BigDecimal;

/** Min and max price of active products; both are null when there are none. */
public record PriceRange(BigDecimal min, BigDecimal max) { }
