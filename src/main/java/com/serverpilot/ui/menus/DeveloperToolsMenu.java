package com.serverpilot.ui.menus;

import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class DeveloperToolsMenu extends Menu {

    public DeveloperToolsMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Developer Tools");
    }

    @Override
    protected int size() {
        return 45;
    }

    @Override
    protected void build(Player viewer) {
        set(19, plannedIcon(Material.SPYGLASS, "Entity Inspector",
                "Full entity state including display entities"));
        set(21, plannedIcon(Material.ENDER_EYE, "Nearby Entity Search",
                "Find and filter entities around you"));
        set(23, plannedIcon(Material.NAME_TAG, "Permission Tester",
                "Check what a node resolves to for any player"));
        set(25, plannedIcon(Material.MAP, "Location Bookmarks",
                "Save and jump between working locations"));

        set(31, Icon.of(services.messenger(), Material.BLAZE_ROD)
                .name("<accent>Available now")
                .lore("<muted>The debug wand under Admin Tools already")
                .lore("<muted>reports blocks and entities.")
                .build());
    }
}
