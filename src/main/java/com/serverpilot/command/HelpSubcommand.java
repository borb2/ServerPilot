package com.serverpilot.command;

import com.serverpilot.Permissions;
import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Services;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public final class HelpSubcommand implements Subcommand {

    private final Services services;
    private final Collection<Subcommand> all;

    public HelpSubcommand(Services services, Collection<Subcommand> all) {
        this.services = services;
        this.all = all;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String usage() {
        return "help";
    }

    @Override
    public String description() {
        return "List the available subcommands";
    }

    @Override
    public String permission() {
        return Permissions.USE;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messenger messenger = services.messenger();
        messenger.sendRaw(sender, "");
        messenger.sendRaw(sender, "<primary>ServerPilot <muted>v<version>",
                Messenger.value("version", services.plugin().getPluginMeta().getVersion()));
        messenger.sendRaw(sender, "<faint>  /sp <muted>· <text>open the dashboard");
        for (Subcommand subcommand : all) {
            if (!sender.hasPermission(subcommand.permission())) {
                continue;
            }
            messenger.sendRaw(sender, "<faint>  /sp <accent><usage> <muted>· <text><description>",
                    Messenger.value("usage", subcommand.usage()),
                    Messenger.value("description", subcommand.description()));
        }
    }
}
