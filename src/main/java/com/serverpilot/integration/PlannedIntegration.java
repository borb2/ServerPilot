package com.serverpilot.integration;

import org.bukkit.plugin.java.JavaPlugin;

public record PlannedIntegration(String displayName, String pluginName, String summary) implements Integration {
    @Override
    public boolean implemented() {
        return false;
    }

    @Override
    public void enable(JavaPlugin plugin) {
        throw new UnsupportedOperationException(displayName + " integration is not implemented");
    }
}
