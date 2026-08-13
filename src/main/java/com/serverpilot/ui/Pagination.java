package com.serverpilot.ui;

import java.util.List;

public final class Pagination {

    public static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public static int pageSize() {
        return CONTENT_SLOTS.length;
    }

    public static int pageCount(int totalEntries) {
        if (totalEntries <= 0) {
            return 1;
        }
        return (totalEntries + pageSize() - 1) / pageSize();
    }

    public static int clampPage(int page, int totalEntries) {
        return Math.clamp(page, 0, pageCount(totalEntries) - 1);
    }

    public static <T> List<T> page(List<T> entries, int page) {
        int safePage = clampPage(page, entries.size());
        int from = safePage * pageSize();
        if (from >= entries.size()) {
            return List.of();
        }
        return entries.subList(from, Math.min(entries.size(), from + pageSize()));
    }

    private Pagination() {
    }
}
