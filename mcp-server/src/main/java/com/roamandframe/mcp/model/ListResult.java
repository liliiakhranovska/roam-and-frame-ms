package com.roamandframe.mcp.model;

import java.util.List;

public record ListResult<T>(List<T> items, int total, boolean truncated, String note) {

    public static <T> ListResult<T> of(List<T> all, int max) {
        boolean truncated = all.size() > max;
        List<T> items = truncated ? all.subList(0, max) : all;
        String note = truncated
                ? "Showing the first " + max + " of " + all.size() + ". Narrow the search with filters to see the rest."
                : null;
        return new ListResult<>(items, all.size(), truncated, note);
    }
}
