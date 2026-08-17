package com.serverpilot.ui;

import com.serverpilot.Keys;
import com.serverpilot.config.ServerPilotConfig;
import com.serverpilot.integration.IntegrationManager;
import com.serverpilot.message.Messenger;
import com.serverpilot.performance.PerformanceService;
import com.serverpilot.tool.DebugWand;
import com.serverpilot.tool.RankMannequins;
import org.bukkit.plugin.java.JavaPlugin;

public record Services(
        JavaPlugin plugin,
        Keys keys,
        ServerPilotConfig config,
        Messenger messenger,
        UiSounds sounds,
        PerformanceService performance,
        IntegrationManager integrations,
        DebugWand debugWand,
        RankMannequins rankMannequins
) {
}
