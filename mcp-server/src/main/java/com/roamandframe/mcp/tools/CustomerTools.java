package com.roamandframe.mcp.tools;

import com.roamandframe.mcp.client.Backend;
import com.roamandframe.mcp.client.PlatformClient;
import com.roamandframe.mcp.model.ProfileView;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
class CustomerTools {
    private static final ParameterizedTypeReference<ProfileView> PROFILE = new ParameterizedTypeReference<>() { };
    private final PlatformClient platform;
    CustomerTools(PlatformClient platform) { this.platform = platform; }

    @McpTool(name = "get_profile",
            description = "Get the current customer's profile: name, email, phone and delivery address.")
    public ProfileView getProfile(McpSyncRequestContext context) {
        return platform.get(Backend.CUSTOMER, Auth.of(context),
                uri -> uri.path("/api/v1/customer/profile").build(), PROFILE);
    }
}
