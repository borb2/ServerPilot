package com.serverpilot.integration;

import org.bukkit.plugin.java.JavaPlugin;

public interface Integration {
    String displayName();
    String pluginName();

    default boolean implemented() {
        return true;
    }

    void enable(JavaPlugin plugin) throws Exception;
    default void disable() {
    }
}
