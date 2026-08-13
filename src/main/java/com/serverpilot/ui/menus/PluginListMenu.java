package com.serverpilot.ui.menus;

import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.PaginatedMenu;
import com.serverpilot.ui.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class PluginListMenu extends PaginatedMenu<Plugin> {
    public PluginListMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Plugins");
    }

    @Override
    protected List<Plugin> entries() {
        return Arrays.stream(services.plugin().getServer().getPluginManager().getPlugins())
                .sorted(Comparator.comparing(Plugin::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    protected ItemStack icon(Plugin entry) {
        boolean enabled = entry.isEnabled();
        return Icon.of(services.messenger(), enabled ? Material.ENCHANTED_BOOK : Material.BOOK)
                .name((enabled ? "<accent>" : "<muted>") + entry.getName())
                .field("Version", entry.getPluginMeta().getVersion())
                .lore("<muted>State: " + (enabled ? "<success>Enabled" : "<error>Disabled"))
                .blank()
                .lore("<faint>Click for details")
                .build();
    }

    @Override
    protected void onSelect(Player viewer, Plugin entry) {
        new PluginInfoMenu(services, this, entry.getName()).open(viewer);
    }

    @Override
    protected ItemStack emptyIcon() {
        return Icon.of(services.messenger(), Material.BARRIER)
                .name("<muted>No plugins loaded")
                .build();
    }
}
