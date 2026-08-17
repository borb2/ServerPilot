package com.serverpilot.ui.menus;

import com.serverpilot.Permissions;
import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ToolsMenu extends Menu {

    public ToolsMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Tools");
    }

    @Override
    protected int size() {
        return 36;
    }

    @Override
    protected void build(Player viewer) {
        set(12, debugWandIcon(viewer), (player, click) -> giveWand(player));
        set(14, rankMannequinIcon(viewer), (player, click) -> spawnMannequins(player));
    }

    private ItemStack debugWandIcon(Player viewer) {
        boolean available = services.config().debugWandEnabled() && viewer.hasPermission(Permissions.TOOL_DEBUG_WAND);
        Icon icon = Icon.of(services.messenger(), services.config().debugWandMaterial())
                .name((available ? "<accent>" : "<muted>") + "Debug Wand")
                .lore("<muted>Inspect blocks and entities in the world")
                .blank();
        if (available) {
            icon.lore("<faint>Click to receive one").glow();
        } else if (!services.config().debugWandEnabled()) {
            icon.lore("<error>Disabled in the config");
        } else {
            icon.lore("<error>No permission");
        }
        return icon.build();
    }

    private ItemStack rankMannequinIcon(Player viewer) {
        boolean allowed = viewer.hasPermission(Permissions.TOOL_TEST_RANKS);
        Icon icon = Icon.of(services.messenger(), Material.ARMOR_STAND)
                .name((allowed ? "<accent>" : "<muted>") + "Rank Preview")
                .lore("<muted>Line up a mannequin wearing your skin")
                .lore("<muted>for every LuckPerms rank")
                .blank()
                .lore("<faint>Click a mannequin for a chat preview")
                .blank();
        return (allowed
                ? icon.lore("<faint>Click to spawn <muted>· <text>/testranks")
                : icon.lore("<error>No permission"))
                .build();
    }

    private void giveWand(Player player) {
        if (!services.config().debugWandEnabled()) {
            services.messenger().error(player, "The debug wand is disabled in the ServerPilot config.");
            services.sounds().deny(player);
            return;
        }
        if (!player.hasPermission(Permissions.TOOL_DEBUG_WAND)) {
            services.messenger().error(player, "You do not have permission to use the debug wand.");
            services.sounds().deny(player);
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
            services.messenger().warning(player, "Your inventory is full.");
            services.sounds().deny(player);
            return;
        }
        player.getInventory().addItem(services.debugWand().create());
        services.messenger().success(player, "Debug wand added to your inventory.");
        services.sounds().confirm(player);
    }

    private void spawnMannequins(Player player) {
        if (!player.hasPermission(Permissions.TOOL_TEST_RANKS)) {
            services.messenger().error(player, "You do not have permission to preview ranks.");
            services.sounds().deny(player);
            return;
        }
        player.closeInventory();
        int spawned = services.rankMannequins().spawn(player);
        if (spawned > 0) {
            services.messenger().success(player, "Spawned <accent><count><success> rank mannequins.",
                    Messenger.value("count", String.valueOf(spawned)));
            services.sounds().confirm(player);
        }
    }
}
