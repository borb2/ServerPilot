package com.serverpilot.config;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class ServerPilotConfig {
    private static final String DEFAULT_PREFIX = "<primary>ServerPilot <faint>»<reset> ";
    private static final Material DEFAULT_WAND_MATERIAL = Material.BLAZE_ROD;

    private final JavaPlugin plugin;
    private final Logger log;

    private boolean showPrefix = true;
    private String prefix = DEFAULT_PREFIX;
    private boolean sounds = true;
    private boolean fillEmptySlots = true;
    private boolean debugWandEnabled = true;
    private Material debugWandMaterial = DEFAULT_WAND_MATERIAL;
    private boolean integrationsEnabled = true;
    private boolean debugLogging;
    public ServerPilotConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.log = plugin.getSLF4JLogger();
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        showPrefix = config.getBoolean("messages.show-prefix", true);
        prefix = validPrefix(config.getString("messages.prefix", DEFAULT_PREFIX));
        sounds = config.getBoolean("ui.sounds", true);
        fillEmptySlots = config.getBoolean("ui.fill-empty-slots", true);
        debugWandEnabled = config.getBoolean("debug-wand.enabled", true);
        debugWandMaterial = validWandMaterial(config.getString("debug-wand.material"));
        integrationsEnabled = config.getBoolean("integrations.enabled", true);
        debugLogging = config.getBoolean("logging.debug", false);
    }

    private String validPrefix(String raw) {
        if (raw == null || raw.isBlank()) {
            return DEFAULT_PREFIX;
        }
        try {
            MiniMessage.miniMessage().deserialize(raw);
            return raw;
        } catch (ParsingException e) {
            log.warn("messages.prefix is not valid MiniMessage ({}), using the default.", e.getMessage());
            return DEFAULT_PREFIX;
        }
    }

    private Material validWandMaterial(String raw) {
        if (raw == null || raw.isBlank()) {
            return DEFAULT_WAND_MATERIAL;
        }
        Material material = Material.matchMaterial(raw);
        if (material == null || !material.isItem()) {
            log.warn("debug-wand.material '{}' is not a valid item, using {}.", raw, DEFAULT_WAND_MATERIAL);
            return DEFAULT_WAND_MATERIAL;
        }
        return material;
    }

    public boolean showPrefix() {
        return showPrefix;
    }

    public String prefix() {
        return prefix;
    }

    public boolean sounds() {
        return sounds;
    }

    public boolean fillEmptySlots() {
        return fillEmptySlots;
    }

    public boolean debugWandEnabled() {
        return debugWandEnabled;
    }

    public Material debugWandMaterial() {
        return debugWandMaterial;
    }

    public boolean integrationsEnabled() {
        return integrationsEnabled;
    }

    public boolean debugLogging() {
        return debugLogging;
    }
}
