package com.serverpilot.ui.menus;

import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Section;
import com.serverpilot.ui.Services;
import com.serverpilot.util.Text;
import io.papermc.paper.ServerBuildInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class ServerMenu extends Menu {

    public ServerMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Server");
    }

    @Override
    protected int size() {
        return 45;
    }

    @Override
    protected void build(Player viewer) {
        Server server = services.plugin().getServer();
        ServerBuildInfo build = ServerBuildInfo.buildInfo();

        Icon software = Icon.of(services.messenger(), Material.BEACON)
                .name("<accent>Software")
                .field("Brand", build.brandName())
                .field("Minecraft", build.minecraftVersionName())
                .field("Bukkit", server.getBukkitVersion());
        build.buildNumber().ifPresent(number -> software.field("Build", String.valueOf(number)));
        set(20, software.build());

        set(21, Icon.of(services.messenger(), Material.PLAYER_HEAD)
                .name("<accent>Players")
                .field("Online", server.getOnlinePlayers().size() + " / " + server.getMaxPlayers())
                .build());

        Icon worlds = Icon.of(services.messenger(), Material.GRASS_BLOCK)
                .name("<accent>Worlds")
                .field("Loaded", String.valueOf(server.getWorlds().size()))
                .blank();
        for (World world : server.getWorlds()) {
            worlds.lore("<faint>· <text>" + world.getName()
                    + " <faint>(" + Text.prettify(world.getEnvironment().name()) + ")");
        }
        set(22, worlds.build());

        set(23, Icon.of(services.messenger(), Material.CLOCK)
                .name("<accent>Uptime")
                .field("Running for", Text.duration(services.performance().uptime()))
                .build());

        set(24, Icon.of(services.messenger(), Material.REDSTONE_TORCH)
                .name("<accent>Performance")
                .lore("<muted>Tick rate, memory and world load")
                .blank()
                .lore("<faint>Click to open")
                .build(), (player, click) -> {
                    if (!Section.PERFORMANCE.allowed(player)) {
                        services.messenger().error(player, "You do not have access to Performance.");
                        services.sounds().deny(player);
                        return;
                    }
                    Section.PERFORMANCE.create(services, this).open(player);
                });
    }
}
