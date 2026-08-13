package com.serverpilot.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextTest {

    @Test
    void bytesScalesToTheLargestFittingUnit() {
        assertEquals("512 B", Text.bytes(512));
        assertEquals("1.0 KB", Text.bytes(1024));
        assertEquals("1.0 MB", Text.bytes(1024L * 1024));
        assertEquals("2.5 GB", Text.bytes((long) (2.5 * 1024 * 1024 * 1024)));
    }

    @Test
    void durationDropsUnitsThatAreZeroOnTheLeft() {
        assertEquals("45s", Text.duration(Duration.ofSeconds(45)));
        assertEquals("2m 5s", Text.duration(Duration.ofSeconds(125)));
        assertEquals("3h 0m", Text.duration(Duration.ofHours(3)));
        assertEquals("1d 2h 3m", Text.duration(Duration.ofDays(1).plusHours(2).plusMinutes(3)));
    }

    @Test
    void durationNeverGoesNegative() {
        assertEquals("0s", Text.duration(Duration.ofSeconds(-10)));
    }

    @Test
    void prettifyTitleCasesConstantNames() {
        assertEquals("Deep Dark", Text.prettify("DEEP_DARK"));
        assertEquals("Stone", Text.prettify("STONE"));
    }

    @Test
    void truncateAddsAnEllipsisOnlyWhenItCuts() {
        assertEquals("short", Text.truncate("short", 10));
        assertEquals("abcd…", Text.truncate("abcdefgh", 5));
    }

    @Test
    void wrapBreaksOnWordsAndRespectsTheLineLimit() {
        List<String> lines = Text.wrap("the quick brown fox jumps over the lazy dog", 12, 10);
        assertEquals(List.of("the quick", "brown fox", "jumps over", "the lazy dog"), lines);
        assertEquals(2, Text.wrap("the quick brown fox jumps over the lazy dog", 12, 2).size());
    }
}
