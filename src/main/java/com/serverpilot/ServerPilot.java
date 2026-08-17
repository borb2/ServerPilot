package com.serverpilot;

import com.serverpilot.command.ServerPilotCommand;
import com.serverpilot.command.TestRanksCommand;
import com.serverpilot.config.ServerPilotConfig;
import com.serverpilot.integration.IntegrationManager;
import com.serverpilot.message.Messenger;
import com.serverpilot.performance.PerformanceService;
import com.serverpilot.tool.DebugWand;
import com.serverpilot.tool.DebugWandListener;
import com.serverpilot.tool.RankMannequins;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.MenuListener;
import com.serverpilot.ui.Services;
import com.serverpilot.ui.UiSounds;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ServerPilot extends JavaPlugin {
    private ServerPilotConfig config;
    private IntegrationManager integrations;
    private RankMannequins rankMannequins;

    @Override
    public void onEnable() {
        config = new ServerPilotConfig(this);
        config.load();
        Keys keys = new Keys(this);
        Messenger messenger = new Messenger(config);
        UiSounds sounds = new UiSounds(config);
        PerformanceService performance = new PerformanceService(getServer());
        integrations = new IntegrationManager(this, config);
        DebugWand debugWand = new DebugWand(keys, config, messenger);
        integrations.enableAll();
        rankMannequins = new RankMannequins(keys, config, messenger, integrations);
        Services services = new Services(this, keys, config, messenger, sounds, performance, integrations,
                debugWand, rankMannequins);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new DebugWandListener(services), this);
        getServer().getPluginManager().registerEvents(rankMannequins, this);

        registerCommand("serverpilot", "Open the ServerPilot dashboard", List.of("sp"),
                new ServerPilotCommand(services));
        registerCommand("testranks", "Spawn a mannequin for every LuckPerms rank",
                new TestRanksCommand(services));

        getSLF4JLogger().info("ServerPilot {} enabled.", getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {

        for (Player player : getServer().getOnlinePlayers()) {
            if (!getServer().isOwnedByCurrentRegion(player)) {
                continue;
            }
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu) {
                player.closeInventory();
            }
        }
        if (rankMannequins != null) {
            rankMannequins.unlistAll();
        }
        if (integrations != null) {
            integrations.disableAll();
        }
    }
}
