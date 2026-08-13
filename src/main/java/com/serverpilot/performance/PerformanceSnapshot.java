package com.serverpilot.performance;

import java.time.Duration;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public record PerformanceSnapshot(
        OptionalDouble tps,
        OptionalDouble mspt,
        boolean regionScoped,
        long usedMemory,
        long maxMemory,
        int players,
        int worlds,
        OptionalInt chunks,
        OptionalInt entities,
        Duration uptime
) {
    public double memoryPercent() {
        return maxMemory <= 0 ? 0 : (usedMemory * 100.0) / maxMemory;
    }
}
