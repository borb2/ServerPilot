package com.serverpilot.ui.menus;

import com.serverpilot.Permissions;
import com.serverpilot.config.ServerPilotConfig;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SettingsMenu extends Menu {
    private static final int SLOT_RELOAD = 44;
    public SettingsMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Settings");
    }

    @Override
    protected int size() {
        return 45;
    }

    @Override
    protected void build(Player viewer) {
        ServerPilotConfig config = services.config();
        set(20, toggle(Material.NAME_TAG, "Message prefix", "messages.show-prefix", config.showPrefix()));
        set(21, toggle(Material.NOTE_BLOCK, "Interface sounds", "ui.sounds", config.sounds()));
        set(22, toggle(Material.GRAY_STAINED_GLASS_PANE, "Fill empty slots", "ui.fill-empty-slots",
                config.fillEmptySlots()));
        set(23, toggle(Material.BLAZE_ROD, "Debug wand", "debug-wand.enabled", config.debugWandEnabled()));
        set(24, toggle(Material.COMPARATOR, "Integrations", "integrations.enabled", config.integrationsEnabled()));
        set(31, toggle(Material.WRITABLE_BOOK, "Debug logging", "logging.debug", config.debugLogging()));
        if (viewer.hasPermission(Permissions.SETTINGS_RELOAD)) {
            set(SLOT_RELOAD, Icon.of(services.messenger(), Material.LIME_DYE)
                    .name("<accent>Reload config")
                    .lore("<muted>Re-read config.yml from disk")
                    .build(), (player, click) -> {
                services.config().load();
                services.messenger().success(player, "Configuration reloaded.");
                services.sounds().confirm(player);
                refresh(player);
            });
        }
    }

    private ItemStack toggle(Material material, String label, String path, boolean enabled) {
        return Icon.of(services.messenger(), material)
                .name("<accent>" + label)
                .lore("<muted>Value: " + (enabled ? "<success>enabled" : "<error>disabled"))
                .field("Config path", path)
                .blank()
                .lore("<faint>Edit config.yml, then reload")
                .build();
    }
}
