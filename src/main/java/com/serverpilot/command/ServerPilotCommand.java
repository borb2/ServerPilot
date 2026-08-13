package com.serverpilot.command;

import com.serverpilot.Permissions;
import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Services;
import com.serverpilot.ui.menus.DashboardMenu;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ServerPilotCommand implements BasicCommand {
    private final Services services;
    private final Map<String, Subcommand> subcommands = new LinkedHashMap<>();

    public ServerPilotCommand(Services services) {
        this.services = services;

        List<Subcommand> registry = new ArrayList<>(List.of(
                new OpenSubcommand(services),
                new DebugWandSubcommand(services),
                new ReloadSubcommand(services)
        ));
        registry.add(new HelpSubcommand(services, List.copyOf(registry)));
        registry.forEach(subcommand -> subcommands.put(subcommand.name(), subcommand));
    }

    @Override
    public void execute(CommandSourceStack source, String [] args) {
        CommandSender sender = source.getSender();
        if (args.length == 0) {
            openDashboard(sender);
            return;
        }
        Subcommand subcommand = subcommands.get(args[0].toLowerCase(Locale.ROOT));
        if (subcommand == null) {
            services.messenger().error(sender, "Unknown subcommand <accent><name><error>. Try <text>/sp help<error>.",
                    Messenger.value("name", args[0]));
            return;
        }
        if (!sender.hasPermission(subcommand.permission())) {
            services.messenger().error(sender, "You do not have permission to do that.");
            return;
        }
        subcommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String [] args) {
        CommandSender sender = source.getSender();
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            return subcommands.values().stream()
                    .filter(subcommand -> sender.hasPermission(subcommand.permission()))
                    .map(Subcommand::name)
                    .filter(name -> name.startsWith(prefix))
                    .toList();
        }
        Subcommand subcommand = subcommands.get(args[0].toLowerCase(Locale.ROOT));
        if (subcommand == null || !sender.hasPermission(subcommand.permission())) {
            return List.of();
        }
        return subcommand.suggest(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public String permission() {
        return Permissions.USE;
    }

    private void openDashboard(CommandSender sender) {
        if (sender instanceof Player player) {
            new DashboardMenu(services).open(player);
            return;
        }
        services.messenger().info(sender, "ServerPilot's dashboard is in-game only. Run <accent>/sp help<text> for "
                + "console-friendly subcommands.");
    }
}
