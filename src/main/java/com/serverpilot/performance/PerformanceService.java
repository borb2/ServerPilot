package com.serverpilot.performance;

import io.papermc.paper.ServerBuildInfo;
import net.kyori.adventure.key.Key;
import org.bukkit.Server;
import org.bukkit.World;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public final class PerformanceService {
    private static final Key FOLIA = Key.key("papermc", "folia");
    private static final long CACHE_MILLIS = 2_000L;
    private final Server server;
    private final boolean folia;
    private PerformanceSnapshot cached;
    private long cachedAt;
    public PerformanceService(Server server) {
        this.server = server;
        this.folia = ServerBuildInfo.buildInfo().isBrandCompatible(FOLIA);
    }

    public boolean regionised() {
        return folia;
    }

    public PerformanceSnapshot snapshot() {
        long now = System.currentTimeMillis();
        if (cached != null && now - cachedAt < CACHE_MILLIS) {
            return cached;
        }
        Runtime runtime = Runtime.getRuntime();
        cached = new PerformanceSnapshot(
                readDouble(() -> server.getTPS()[0]),
                readDouble(server::getAverageTickTime),
                folia,
                runtime.totalMemory() - runtime.freeMemory(),
                runtime.maxMemory(),
                server.getOnlinePlayers().size(),
                server.getWorlds().size(),
                readInt(() -> sum(World::getChunkCount)),
                readInt(() -> sum(World::getEntityCount)),
                uptime()
        );
        cachedAt = now;
        return cached;
    }

    public Duration uptime() {
        return Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
    }

    private int sum(java.util.function.ToIntFunction<World> counter) {
        int total = 0;
        for (World world : server.getWorlds()) {
            total += counter.applyAsInt(world);
        }
        return total;
    }

    private OptionalDouble readDouble(DoubleSupplier supplier) {
        try {
            return OptionalDouble.of(supplier.getAsDouble());
        } catch (UnsupportedOperationException e) {
            return OptionalDouble.empty();
        }
    }

    private OptionalInt readInt(IntSupplier supplier) {
        try {
            return OptionalInt.of(supplier.getAsInt());
        } catch (UnsupportedOperationException e) {
            return OptionalInt.empty();
        }
    }
}
