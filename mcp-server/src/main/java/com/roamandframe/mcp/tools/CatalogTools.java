package com.roamandframe.mcp.tools;

import com.roamandframe.mcp.client.Backend;
import com.roamandframe.mcp.client.PlatformClient;
import com.roamandframe.mcp.client.ToolException;
import com.roamandframe.mcp.model.ListResult;
import com.roamandframe.mcp.model.ProductDetail;
import com.roamandframe.mcp.model.ProductSummary;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
class CatalogTools {

    private static final ParameterizedTypeReference<List<ProductSummary>> PRODUCT_LIST =
            new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<ProductDetail> PRODUCT_DETAIL = new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<Map<String, Object>> FILTERS = new ParameterizedTypeReference<>() { };

    private final PlatformClient platform;
    private final int maxItems;

    CatalogTools(PlatformClient platform, @Value("${platform.max-items}") int maxItems) {
        this.platform = platform;
        this.maxItems = maxItems;
    }

    @McpTool(name = "search_products",
            description = "Search the Roam & Frame product catalogue. All filters are optional and can be combined. "
                    + "Returns a short list with sku, name, brand, price, category and stock quantity. "
                    + "Use get_catalog_filters first if you need valid category codes or brands.")
    public ListResult<ProductSummary> searchProducts(
            McpSyncRequestContext context,
            @McpToolParam(description = "Words to look for in the product name or description", required = false) String text,
            @McpToolParam(description = "Category codes, e.g. lenses, cameras (see get_catalog_filters)", required = false) List<String> categories,
            @McpToolParam(description = "Brand names, e.g. Aperture. Not case sensitive", required = false) List<String> brands,
            @McpToolParam(description = "Minimum price", required = false) BigDecimal minPrice,
            @McpToolParam(description = "Maximum price", required = false) BigDecimal maxPrice,
            @McpToolParam(description = "true to return only waterproof products", required = false) Boolean waterproof,
            @McpToolParam(description = "true to return only weather-sealed products", required = false) Boolean weatherSealed,
            @McpToolParam(description = "Target gender, e.g. unisex", required = false) String gender,
            @McpToolParam(description = "Maximum weight in grams", required = false) Integer maxWeightG,
            @McpToolParam(description = "true for products in stock only, false for out of stock only", required = false) Boolean inStock,
            @McpToolParam(description = "Sort by: name, price or createdAt. Default name", required = false) String sort,
            @McpToolParam(description = "asc or desc. Default asc", required = false) String direction) {

        String authorization = Auth.of(context);;

        List<ProductSummary> products = platform.get(Backend.CATALOG, authorization, uri -> {
            var b = uri.path("/api/v1/products");
            if (text != null) b.queryParam("q", text);
            if (categories != null && !categories.isEmpty()) b.queryParam("category", categories);
            if (brands != null && !brands.isEmpty()) b.queryParam("brand", brands);
            if (minPrice != null) b.queryParam("minPrice", minPrice);
            if (maxPrice != null) b.queryParam("maxPrice", maxPrice);
            if (waterproof != null) b.queryParam("waterproof", waterproof);
            if (weatherSealed != null) b.queryParam("weatherSealed", weatherSealed);
            if (gender != null) b.queryParam("gender", gender);
            if (maxWeightG != null) b.queryParam("maxWeightG", maxWeightG);
            if (inStock != null) b.queryParam("inStock", inStock);
            if (sort != null) b.queryParam("sort", sort);
            if (direction != null) b.queryParam("direction", direction);
            return b.build();
        }, PRODUCT_LIST);

        return ListResult.of(products, maxItems);
    }

    @McpTool(name = "get_product",
            description = "Get full details of one product by SKU: description, price, all attributes "
                    + "(waterproof, weight, gender, ...) and stock quantity.")
    public ProductDetail getProduct(
            McpSyncRequestContext context,
            @McpToolParam(description = "Product SKU, e.g. as returned by search_products") String sku) {

        return fetchProduct(Auth.of(context), sku);
    }

    @McpTool(name = "compare_products",
            description = "Compare 2 to 5 products side by side. Returns full details of each, "
                    + "so differences in price, attributes and stock can be described.")
    public List<ProductDetail> compareProducts(
            McpSyncRequestContext context,
            @McpToolParam(description = "2 to 5 product SKUs") List<String> skus) {

        if (skus == null || skus.size() < 2 || skus.size() > 5) {
            throw new ToolException("Provide between 2 and 5 SKUs to compare.");
        }
        String authorization = Auth.of(context);
        return skus.stream().distinct().map(sku -> fetchProduct(authorization, sku)).toList();
    }

    @McpTool(name = "get_catalog_filters",
            description = "List valid filter values for search_products: category codes, brands, price range, "
                    + "and the product attributes that can be filtered. Call this before filtering by category or brand.")
    public Map<String, Object> getCatalogFilters(McpSyncRequestContext context) {
        return platform.get(Backend.CATALOG, Auth.of(context),
                uri -> uri.path("/api/v1/products/filters").build(), FILTERS);
    }

    private ProductDetail fetchProduct(String authorization, String sku) {
        try {
            return platform.get(Backend.CATALOG, authorization,
                    uri -> uri.path("/api/v1/products/{sku}").build(sku), PRODUCT_DETAIL);
        } catch (ToolException e) {
            throw new ToolException("Product '" + sku + "': " + e.getMessage());
        }
    }
}
