package com.roamandframe.cart.client;

import java.util.Collection;
import java.util.Map;

public interface CatalogClient {
    /** Products that exist, by SKU, with their stock; unknown SKUs are missing from the map. */
    Map<String, CatalogProduct> getProducts(Collection<String> skus);
}
