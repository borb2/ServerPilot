package com.serverpilot.ui.menus;

import com.serverpilot.performance.PerformanceSnapshot;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Services;
import com.serverpilot.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.OptionalInt;

public final class PerformanceMenu extends Menu {

    private static final int SLOT_REFRESH = 44;

    public PerformanceMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Performance");
    }

    @Override
    protected int size() {
        return 45;
    }

    @Override
    protected void build(Player viewer) {
        PerformanceSnapshot snapshot = services.performance().snapshot();
        boolean regionised = snapshot.regionScoped();

        Icon tps = Icon.of(services.messenger(), Material.CLOCK).name("<accent>Tick rate");
        if (snapshot.tps().isPresent()) {
            double value = snapshot.tps().getAsDouble();
            tps.lore("<muted>TPS: " + tpsColour(value) + Text.decimal(Math.min(value, 20.0), 2))
                    .blank()
                    .lore(regionised
                            ? "<faint>Your current region, not the whole server"
                            : "<faint>Average over the last minute");
        } else {
            unavailable(tps, "Tick rate belongs to a region here");
        }
        set(20, tps.build());

        Icon mspt = Icon.of(services.messenger(), Material.REPEATER).name("<accent>Tick time");
        if (snapshot.mspt().isPresent()) {
            double value = snapshot.mspt().getAsDouble();
            mspt.lore("<muted>MSPT: " + msptColour(value) + Text.decimal(value, 2) + " ms")
                    .blank()
                    .lore("<faint>Anything above 50 ms drops the tick rate");
        } else {
            unavailable(mspt, "Tick time belongs to a region here");
        }
        set(22, mspt.build());

        set(24, Icon.of(services.messenger(), Material.CHEST)
                .name("<accent>Memory")
                .field("Used", Text.bytes(snapshot.usedMemory()))
                .field("Max", Text.bytes(snapshot.maxMemory()))
                .field("Usage", Text.decimal(snapshot.memoryPercent(), 1) + "%")
                .build());

        Icon worlds = Icon.of(services.messenger(), Material.GRASS_BLOCK)
                .name("<accent>Worlds")
                .field("Loaded worlds", String.valueOf(snapshot.worlds()));
        set(29, counted(worlds, "Loaded chunks", snapshot.chunks(), regionised).build());

        Icon entities = Icon.of(services.messenger(), Material.EGG)
                .name("<accent>Entities")
                .field("Players", String.valueOf(snapshot.players()));
        set(31, counted(entities, "Total entities", snapshot.entities(), regionised).build());

        set(33, Icon.of(services.messenger(), Material.CLOCK)
                .name("<accent>Uptime")
                .field("Running for", Text.duration(snapshot.uptime()))
                .build());

        set(SLOT_REFRESH, Icon.of(services.messenger(), Material.LIME_DYE)
                .name("<accent>Refresh")
                .lore("<faint>Values are cached for a couple of seconds")
                .build(), (player, click) -> refresh(player));
    }

    private Icon counted(Icon icon, String label, OptionalInt value, boolean regionised) {
        if (value.isEmpty()) {
            return unavailable(icon, "Counted per region here");
        }
        icon.field(label, String.valueOf(value.getAsInt()));
        if (regionised) {
            icon.blank().lore("<faint>Approximate: regions tick independently");
        }
        return icon;
    }

    private Icon unavailable(Icon icon, String reason) {
        return icon.lore("<muted>Value: <faint>unavailable")
                .blank()
                .lore("<faint>" + reason);
    }

    private String tpsColour(double tps) {
        if (tps >= 19.5) {
            return "<success>";
        }
        return tps >= 17.0 ? "<warning>" : "<error>";
    }

    private String msptColour(double mspt) {
        if (mspt <= 25.0) {
            return "<success>";
        }
        return mspt <= 45.0 ? "<warning>" : "<error>";
    }
}
