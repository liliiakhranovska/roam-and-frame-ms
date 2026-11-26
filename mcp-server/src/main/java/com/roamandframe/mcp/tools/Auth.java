package com.roamandframe.mcp.tools;

import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;

final class Auth {
    private Auth() { }

    static String of(McpSyncRequestContext context) {
        return (String) context.transportContext().get("authorization");
    }
}
