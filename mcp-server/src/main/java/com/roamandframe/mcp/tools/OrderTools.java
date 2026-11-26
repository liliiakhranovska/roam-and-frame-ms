package com.roamandframe.mcp.tools;

import com.roamandframe.mcp.client.Backend;
import com.roamandframe.mcp.client.PlatformClient;
import com.roamandframe.mcp.client.ToolException;
import com.roamandframe.mcp.model.ListResult;
import com.roamandframe.mcp.model.OrderView;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Component
class OrderTools {

    private static final ParameterizedTypeReference<List<OrderView>> ORDER_LIST = new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<OrderView> ORDER = new ParameterizedTypeReference<>() { };

    private final PlatformClient platform;
    private final int maxItems;

    OrderTools(PlatformClient platform, @Value("${platform.max-items}") int maxItems) {
        this.platform = platform;
        this.maxItems = maxItems;
    }

    @McpTool(name = "list_orders",
            description = "List the current customer's orders (placed or shipped). All filters are optional. "
                    + "Dates are calendar dates (YYYY-MM-DD) in the customer's timezone, both ends inclusive. "
                    + "Use createdFrom/createdTo for when the order was placed and shippedFrom/shippedTo for when it was shipped. "
                    + "For 'last month' or 'this year' work out the dates yourself from today's date.")
    public ListResult<OrderView> listOrders(
            McpSyncRequestContext context,
            @McpToolParam(description = "PLACED or SHIPPED", required = false) List<String> status,
            @McpToolParam(description = "Placed on or after this date, YYYY-MM-DD", required = false) String createdFrom,
            @McpToolParam(description = "Placed on or before this date, YYYY-MM-DD", required = false) String createdTo,
            @McpToolParam(description = "Shipped on or after this date, YYYY-MM-DD", required = false) String shippedFrom,
            @McpToolParam(description = "Shipped on or before this date, YYYY-MM-DD", required = false) String shippedTo,
            @McpToolParam(description = "IANA timezone for the dates, e.g. Europe/Kyiv. Default UTC", required = false) String timezone,
            @McpToolParam(description = "Minimum order total", required = false) BigDecimal minTotal,
            @McpToolParam(description = "Maximum order total", required = false) BigDecimal maxTotal,
            @McpToolParam(description = "Only orders containing this product SKU", required = false) String sku,
            @McpToolParam(description = "Sort by: createdAt, shippedAt or total. Default createdAt", required = false) String sort,
            @McpToolParam(description = "asc or desc. Default desc", required = false) String direction) {

        ZoneId zone = zone(timezone);
        String cFrom = start(createdFrom, zone);
        String cBefore = endExclusive(createdTo, zone);
        String sFrom = start(shippedFrom, zone);
        String sBefore = endExclusive(shippedTo, zone);

        List<OrderView> orders = platform.get(Backend.ORDERS, Auth.of(context), uri -> {
            var b = uri.path("/api/v1/orders");
            if (status != null && !status.isEmpty()) b.queryParam("status", status);
            if (cFrom != null) b.queryParam("createdFrom", cFrom);
            if (cBefore != null) b.queryParam("createdBefore", cBefore);
            if (sFrom != null) b.queryParam("shippedFrom", sFrom);
            if (sBefore != null) b.queryParam("shippedBefore", sBefore);
            if (minTotal != null) b.queryParam("minTotal", minTotal);
            if (maxTotal != null) b.queryParam("maxTotal", maxTotal);
            if (sku != null) b.queryParam("sku", sku);
            if (sort != null) b.queryParam("sort", sort);
            if (direction != null) b.queryParam("direction", direction);
            return b.build();
        }, ORDER_LIST);

        return ListResult.of(orders, maxItems);
    }

    @McpTool(name = "get_order",
            description = "Get one of the current customer's orders by its id, with items and shipment status.")
    public OrderView getOrder(
            McpSyncRequestContext context,
            @McpToolParam(description = "Order id (UUID), as returned by list_orders") String orderId) {

        UUID id;
        try {
            id = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new ToolException("orderId must be a UUID such as the ids returned by list_orders.");
        }
        return platform.get(Backend.ORDERS, Auth.of(context),
                uri -> uri.path("/api/v1/orders/{id}").build(id), ORDER);
    }

    private static ZoneId zone(String timezone) {
        if (timezone == null || timezone.isBlank()) return ZoneOffset.UTC;
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new ToolException("Unknown timezone '" + timezone + "'. Use an IANA name such as Europe/Kyiv.");
        }
    }

    /** Start of the given local day, as a UTC instant string (…Z). */
    private static String start(String date, ZoneId zone) {
        LocalDate d = parse(date);
        return d == null ? null : d.atStartOfDay(zone).toInstant().toString();
    }

    /** Start of the day AFTER the given date: the exclusive upper bound that makes "to" inclusive. */
    private static String endExclusive(String date, ZoneId zone) {
        LocalDate d = parse(date);
        return d == null ? null : d.plusDays(1).atStartOfDay(zone).toInstant().toString();
    }

    private static LocalDate parse(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new ToolException("Invalid date '" + date + "'. Use YYYY-MM-DD.");
        }
    }
}
