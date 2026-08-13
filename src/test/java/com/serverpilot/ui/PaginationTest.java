package com.serverpilot.ui;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationTest {

    private static List<Integer> entries(int count) {
        return IntStream.range(0, count).boxed().toList();
    }

    @Test
    void emptyListStillHasOnePage() {
        assertEquals(1, Pagination.pageCount(0));
        assertTrue(Pagination.page(List.of(), 3).isEmpty());
    }

    @Test
    void partialLastPageCounts() {
        assertEquals(1, Pagination.pageCount(Pagination.pageSize()));
        assertEquals(2, Pagination.pageCount(Pagination.pageSize() + 1));
    }

    @Test
    void pagesCoverEveryEntryExactlyOnce() {
        List<Integer> all = entries(Pagination.pageSize() * 2 + 5);
        int seen = 0;
        for (int page = 0; page < Pagination.pageCount(all.size()); page++) {
            seen += Pagination.page(all, page).size();
        }
        assertEquals(all.size(), seen);
    }

    @Test
    void outOfRangePagesClampInsteadOfThrowing() {
        List<Integer> all = entries(5);
        assertEquals(all, Pagination.page(all, -3));
        assertEquals(all, Pagination.page(all, 99));
    }

    @Test
    void contentSlotsMatchThePageSizeAndFitAChest() {
        assertEquals(Pagination.CONTENT_SLOTS.length, Pagination.pageSize());
        for (int slot : Pagination.CONTENT_SLOTS) {
            assertTrue(slot >= 0 && slot < 54, "slot out of range: " + slot);
        }
    }
}
