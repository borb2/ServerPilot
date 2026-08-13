package com.serverpilot.ui.menus;

import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Services;
import com.serverpilot.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.UUID;

public final class PlayerInfoMenu extends Menu {
    private record Snapshot(
            String name,
            UUID id,
            String world,
            double x,
            double y,
            double z,
            GameMode gameMode,
            double health,
            double maxHealth,
            int food,
            int ping,
            Duration playtime,
            boolean operator
    ) {
        static Snapshot of(Player player) {
            AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
            return new Snapshot(
                    player.getName(),
                    player.getUniqueId(),
                    player.getWorld().getName(),
                    player.getLocation().getX(),
                    player.getLocation().getY(),
                    player.getLocation().getZ(),
                    player.getGameMode(),
                    player.getHealth(),
                    maxHealth == null ? player.getHealth() : maxHealth.getValue(),
                    player.getFoodLevel(),
                    player.getPing(),
                    Duration.ofSeconds(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20L),
                    player.isOp()
            );
        }
    }

    private final UUID targetId;
    private final String fallbackName;
    private Snapshot snapshot;
    private boolean awaitingSnapshot;
    public PlayerInfoMenu(Services services, Menu parent, UUID targetId, String targetName) {
        super(services, parent);
        this.targetId = targetId;
        this.fallbackName = targetName;
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Player <muted>· <text><name>",
                Messenger.value("name", Text.truncate(fallbackName, 24)));
    }

    @Override
    protected int size() {
        return 45;
    }

    @Override
    protected void build(Player viewer) {
        Player target = services.plugin().getServer().getPlayer(targetId);
        if (target == null) {
            set(22, offlineIcon());
            return;
        }
        if (services.plugin().getServer().isOwnedByCurrentRegion(target)) {
            snapshot = Snapshot.of(target);
        } else if (snapshot == null) {
            set(22, requestSnapshot(viewer, target)
                    ? Icon.of(services.messenger(), Material.CLOCK)
                    .name("<muted>Reading player state")
                    .lore("<faint>Waiting on the region that owns <name>",
                            Messenger.value("name", fallbackName))
                    .build()
                    : offlineIcon());
            return;
        }
        set(13, Icon.of(services.messenger(), Material.PLAYER_HEAD)
                .skull(target)
                .name("<accent>" + snapshot.name())
                .field("UUID", snapshot.id().toString())
                .build());
        set(20, Icon.of(services.messenger(), Material.COMPASS)
                .name("<accent>Location")
                .field("World", snapshot.world())
                .field("Position", Text.coordinates(snapshot.x(), snapshot.y(), snapshot.z()))
                .field("Game mode", Text.prettify(snapshot.gameMode().name()))
                .build());
        set(22, Icon.of(services.messenger(), Material.GOLDEN_APPLE)
                .name("<accent>Vitals")
                .field("Health", Text.decimal(snapshot.health(), 1) + " / " + Text.decimal(snapshot.maxHealth(), 1))
                .field("Food", String.valueOf(snapshot.food()))
                .field("Ping", snapshot.ping() + " ms")
                .build());
        set(24, Icon.of(services.messenger(), Material.CLOCK)
                .name("<accent>Session")
                .field("Playtime", Text.duration(snapshot.playtime()))
                .field("Operator", snapshot.operator() ? "Yes" : "No")
                .build());
    }

    private ItemStack offlineIcon() {
        return Icon.of(services.messenger(), Material.BARRIER)
                .name("<error>Player offline")
                .lore("<muted><name> left the server.", Messenger.value("name", fallbackName))
                .build();
    }

    private boolean requestSnapshot(Player viewer, Player target) {
        if (awaitingSnapshot) {
            return true;
        }
        awaitingSnapshot = true;
        var scheduled = target.getScheduler().run(services.plugin(), task -> {
            Snapshot captured = Snapshot.of(target);
            viewer.getScheduler().run(services.plugin(), applyTask -> {
                snapshot = captured;
                awaitingSnapshot = false;
                if (viewer.getOpenInventory().getTopInventory().getHolder() == this) {
                    refresh(viewer);
                }
            }, () -> awaitingSnapshot = false);
        }, () -> awaitingSnapshot = false);
        if (scheduled == null) {
            awaitingSnapshot = false;
            return false;
        }
        return true;
    }
}
