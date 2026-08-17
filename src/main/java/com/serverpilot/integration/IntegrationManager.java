package com.serverpilot.integration;

import com.serverpilot.config.ServerPilotConfig;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class IntegrationManager {
    private final JavaPlugin plugin;
    private final ServerPilotConfig config;
    private final List<Integration> roster;
    private final Map<String, IntegrationStatus> statuses = new LinkedHashMap<>();
    public IntegrationManager(JavaPlugin plugin, ServerPilotConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.roster = defaultRoster();
    }

    private static List<Integration> defaultRoster() {
        return List.of(
                new LuckPermsIntegration(),
                new PlannedIntegration("WorldEdit", "WorldEdit", "Read and drive the current selection"),
                new PlannedIntegration("WorldGuard", "WorldGuard", "Browse and edit regions"),
                new PlannedIntegration("PlaceholderAPI", "PlaceholderAPI", "Expose ServerPilot placeholders"),
                new PlannedIntegration("CoreProtect", "CoreProtect", "Block history from the debug wand"),
                new PlannedIntegration("DecentHolograms", "DecentHolograms", "Move and edit holograms visually"),
                new PlannedIntegration("Chunky", "Chunky", "Control pregeneration from the dashboard"),
                new PlannedIntegration("spark", "spark", "Deep profiling links from Performance")
        );
    }

    public void enableAll() {
        statuses.clear();
        for (Integration integration : roster) {
            statuses.put(integration.pluginName(), resolve(integration));
        }
        long active = statuses.values().stream().filter(status -> status == IntegrationStatus.ACTIVE).count();
        if (config.debugLogging()) {
            statuses.forEach((name, status) -> plugin.getSLF4JLogger().info("Integration {}: {}", name, status.label()));
        }
        if (active > 0) {
            plugin.getSLF4JLogger().info("{} integration(s) active.", active);
        }
    }

    private IntegrationStatus resolve(Integration integration) {
        if (!config.integrationsEnabled()) {
            return IntegrationStatus.DISABLED;
        }
        if (!integration.implemented()) {
            return installed(integration) ? IntegrationStatus.PLANNED : IntegrationStatus.NOT_INSTALLED;
        }
        if (!installed(integration)) {
            return IntegrationStatus.NOT_INSTALLED;
        }
        try {
            integration.enable(plugin);
            return IntegrationStatus.ACTIVE;
        } catch (Exception e) {
            plugin.getSLF4JLogger().error("Integration {} failed to enable.", integration.displayName(), e);
            return IntegrationStatus.FAILED;
        }
    }

    public void disableAll() {
        for (Integration integration : roster) {
            if (status(integration) != IntegrationStatus.ACTIVE) {
                continue;
            }
            try {
                integration.disable();
            } catch (Exception e) {
                plugin.getSLF4JLogger().error("Integration {} failed to disable cleanly.", integration.displayName(), e);
            }
        }
        statuses.clear();
    }

    public boolean installed(Integration integration) {
        Plugin dependency = plugin.getServer().getPluginManager().getPlugin(integration.pluginName());
        return dependency != null && dependency.isEnabled();
    }

    public List<Integration> roster() {
        return roster;
    }

    public IntegrationStatus status(Integration integration) {
        return statuses.getOrDefault(integration.pluginName(), IntegrationStatus.NOT_INSTALLED);
    }

    public <T extends Integration> Optional<T> active(Class<T> type) {
        return roster.stream()
                .filter(integration -> status(integration) == IntegrationStatus.ACTIVE)
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    public Optional<String> version(Integration integration) {
        Plugin dependency = plugin.getServer().getPluginManager().getPlugin(integration.pluginName());
        return Optional.ofNullable(dependency).map(dep -> dep.getPluginMeta().getVersion());
    }
}
