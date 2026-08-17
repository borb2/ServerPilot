package com.serverpilot.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RankMannequinsTest {

    @Test
    void rowIsCentredOnTheSpawnPoint() {
        assertEquals(0.0, RankMannequins.offsetFor(0, 1, 2.0));
        assertEquals(-1.0, RankMannequins.offsetFor(0, 2, 2.0));
        assertEquals(1.0, RankMannequins.offsetFor(1, 2, 2.0));
        assertEquals(-2.0, RankMannequins.offsetFor(0, 3, 2.0));
        assertEquals(0.0, RankMannequins.offsetFor(1, 3, 2.0));
        assertEquals(2.0, RankMannequins.offsetFor(2, 3, 2.0));
    }

    @Test
    void neighboursAreSpacingApart() {
        for (double spacing : new double[]{1.0, 2.0, 3.5}) {
            for (int count = 1; count <= 12; count++) {
                for (int i = 1; i < count; i++) {
                    assertEquals(spacing,
                            RankMannequins.offsetFor(i, count, spacing)
                                    - RankMannequins.offsetFor(i - 1, count, spacing),
                            1e-9);
                }
            }
        }
    }
}
