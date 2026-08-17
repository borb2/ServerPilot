package com.serverpilot;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class Keys {

    public final NamespacedKey tool;
    public final NamespacedKey rankMannequin;

    public Keys(Plugin plugin) {
        this.tool = new NamespacedKey(plugin, "tool");
        this.rankMannequin = new NamespacedKey(plugin, "rank_mannequin");
    }
}
