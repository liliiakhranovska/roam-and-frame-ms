package com.roamandframe.catalog.client;

import java.util.Collection;
import java.util.Map;

public interface InventoryClient {
    /** Quantity per SKU; 0 for a SKU Inventory does not know. */
    Map<String, Integer> getQuantities(Collection<String> skus);
}
