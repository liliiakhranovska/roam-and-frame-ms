package com.roamandframe.mcp.config;

import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerStreamableHttpProperties;
import org.springframework.ai.mcp.server.webmvc.transport.WebMvcStreamableServerTransportProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Configuration
class McpTransportConfig {

    /**
     * Replaces Spring AI's own transport bean (it backs off when one exists). Same settings as the
     * auto-configuration, plus: copy the Authorization header into the transport context so tools can
     * forward it to the platform services.
     */
    @Bean
    WebMvcStreamableServerTransportProvider authAwareTransportProvider(
            JsonMapper jsonMapper, McpServerStreamableHttpProperties properties) {
        return WebMvcStreamableServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(jsonMapper))
                .mcpEndpoint(properties.getMcpEndpoint())
                .keepAliveInterval(properties.getKeepAliveInterval())
                .disallowDelete(properties.isDisallowDelete())
                .contextExtractor(request -> {
                    String authorization = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);
                    if (authorization == null) {
                        return McpTransportContext.EMPTY;
                    }
                    return McpTransportContext.create(Map.of("authorization", authorization));
                })
                .build();
    }
}
