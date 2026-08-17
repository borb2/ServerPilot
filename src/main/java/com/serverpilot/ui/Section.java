package com.serverpilot.ui;

import com.serverpilot.Permissions;
import com.serverpilot.ui.menus.IntegrationsMenu;
import com.serverpilot.ui.menus.PerformanceMenu;
import com.serverpilot.ui.menus.PlayerListMenu;
import com.serverpilot.ui.menus.PluginListMenu;
import com.serverpilot.ui.menus.ServerMenu;
import com.serverpilot.ui.menus.SettingsMenu;
import com.serverpilot.ui.menus.ToolsMenu;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

public enum Section {
    SERVER("server", "Server", Material.BEACON, Permissions.SECTION_SERVER,
            "Software, worlds, players and uptime", ServerMenu::new),
    PLAYERS("players", "Players", Material.PLAYER_HEAD, Permissions.SECTION_PLAYERS,
            "Browse and inspect online players", PlayerListMenu::new),
    TOOLS("tools", "Tools", Material.WOODEN_AXE, Permissions.SECTION_TOOLS,
            "Admin and developer utilities", ToolsMenu::new),
    PLUGINS("plugins", "Plugins", Material.BOOKSHELF, Permissions.SECTION_PLUGINS,
            "Installed plugins and their details", PluginListMenu::new),
    INTEGRATIONS("integrations", "Integrations", Material.COMPARATOR, Permissions.SECTION_INTEGRATIONS,
            "Supported third-party plugins and their status", IntegrationsMenu::new),
    PERFORMANCE("performance", "Performance", Material.CLOCK, Permissions.SECTION_PERFORMANCE,
            "Tick rate, memory, chunks and entities", PerformanceMenu::new),
    SETTINGS("settings", "Settings", Material.REPEATER, Permissions.SECTION_SETTINGS,
            "ServerPilot's own configuration", SettingsMenu::new);
    private final String id;
    private final String displayName;
    private final Material icon;
    private final String permission;
    private final String description;
    private final BiFunction<Services, Menu, Menu> factory;
    Section(String id, String displayName, Material icon, String permission, String description,
            BiFunction<Services, Menu, Menu> factory) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.permission = permission;
        this.description = description;
        this.factory = factory;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Material icon() {
        return icon;
    }

    public String permission() {
        return permission;
    }

    public String description() {
        return description;
    }

    public Menu create(Services services, Menu parent) {
        return factory.apply(services, parent);
    }

    public boolean allowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public static Optional<Section> byId(String id) {
        String needle = id.toLowerCase(Locale.ROOT);
        for (Section section : values()) {
            if (section.id.equals(needle)) {
                return Optional.of(section);
            }
        }
        return Optional.empty();
    }

    public static List<String> ids(CommandSender sender) {
        return java.util.Arrays.stream(values())
                .filter(section -> section.allowed(sender))
                .map(Section::id)
                .toList();
    }
}
