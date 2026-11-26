package com.roamandframe.mcp.client;

public enum Backend {
    CATALOG("catalog"), ORDERS("orders"), CART("cart"), CUSTOMER("customer");

    private final String label;

    Backend(String label) {
        this.label = label;
    }

    String label() {
        return label;
    }
}
