package com.serverpilot;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class Keys {
    public final NamespacedKey tool;
    public final NamespacedKey guiItem;

    public Keys(Plugin plugin) {
        this.tool = new NamespacedKey(plugin, "tool");
        this.guiItem = new NamespacedKey(plugin, "gui_item");
    }
}
