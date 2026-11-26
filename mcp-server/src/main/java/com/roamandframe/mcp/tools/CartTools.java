package com.roamandframe.mcp.tools;

import com.roamandframe.mcp.client.Backend;
import com.roamandframe.mcp.client.PlatformClient;
import com.roamandframe.mcp.model.CartView;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
class CartTools {
    private static final ParameterizedTypeReference<CartView> CART = new ParameterizedTypeReference<>() { };
    private final PlatformClient platform;
    CartTools(PlatformClient platform) { this.platform = platform; }

    @McpTool(name = "get_cart",
            description = "Show the current customer's shopping cart: items, quantities, prices and total. Read only.")
    public CartView getCart(McpSyncRequestContext context) {
        return platform.get(Backend.CART, Auth.of(context), uri -> uri.path("/api/v1/cart").build(), CART);
    }
}