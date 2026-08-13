package com.serverpilot.command;

import com.serverpilot.Permissions;
import com.serverpilot.ui.Services;
import org.bukkit.command.CommandSender;

public final class ReloadSubcommand implements Subcommand {

    private final Services services;

    public ReloadSubcommand(Services services) {
        this.services = services;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String usage() {
        return "reload";
    }

    @Override
    public String description() {
        return "Re-read config.yml from disk";
    }

    @Override
    public String permission() {
        return Permissions.SETTINGS_RELOAD;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            services.config().load();
            services.messenger().success(sender, "Configuration reloaded.");
        } catch (RuntimeException e) {
            services.messenger().error(sender, "Reload failed, see the console for details.");
            services.plugin().getSLF4JLogger().error("Failed to reload ServerPilot configuration.", e);
        }
    }
}
