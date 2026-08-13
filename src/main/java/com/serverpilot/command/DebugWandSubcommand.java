package com.serverpilot.command;

import com.serverpilot.Permissions;
import com.serverpilot.ui.Services;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DebugWandSubcommand implements Subcommand {

    private final Services services;

    public DebugWandSubcommand(Services services) {
        this.services = services;
    }

    @Override
    public String name() {
        return "debugwand";
    }

    @Override
    public String usage() {
        return "debugwand";
    }

    @Override
    public String description() {
        return "Give yourself the debug wand";
    }

    @Override
    public String permission() {
        return Permissions.TOOL_DEBUG_WAND;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            services.messenger().error(sender, "Only a player can receive the debug wand.");
            return;
        }
        if (!services.config().debugWandEnabled()) {
            services.messenger().error(sender, "The debug wand is disabled in the ServerPilot config.");
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
            services.messenger().warning(sender, "Your inventory is full.");
            services.sounds().deny(player);
            return;
        }
        player.getInventory().addItem(services.debugWand().create());
        services.messenger().success(sender, "Debug wand added to your inventory.");
        services.sounds().confirm(player);
    }
}
