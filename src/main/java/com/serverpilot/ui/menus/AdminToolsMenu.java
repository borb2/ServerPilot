package com.serverpilot.ui.menus;

import com.serverpilot.Permissions;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class AdminToolsMenu extends Menu {

    public AdminToolsMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Admin Tools");
    }

    @Override
    protected int size() {
        return 45;
    }

    @Override
    protected void build(Player viewer) {
        boolean available = services.config().debugWandEnabled() && viewer.hasPermission(Permissions.TOOL_DEBUG_WAND);

        Icon wand = Icon.of(services.messenger(), services.config().debugWandMaterial())
                .name((available ? "<accent>" : "<muted>") + "Debug Wand")
                .lore("<muted>Inspect blocks and entities in the world")
                .blank();
        if (available) {
            wand.lore("<faint>Click to receive one").glow();
        } else if (!services.config().debugWandEnabled()) {
            wand.lore("<error>Disabled in the config");
        } else {
            wand.lore("<error>No permission");
        }
        set(20, wand.build(), (player, click) -> giveWand(player));

        set(22, plannedIcon(Material.BELL, "Broadcast",
                "Send a themed announcement to everyone"));
        set(24, plannedIcon(Material.IRON_DOOR, "Maintenance Mode",
                "Close the server to non-staff without stopping it"));
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
}
