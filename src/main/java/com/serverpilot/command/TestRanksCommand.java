package com.serverpilot.command;

import com.serverpilot.Permissions;
import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Services;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class TestRanksCommand implements BasicCommand {

    private final Services services;

    public TestRanksCommand(Services services) {
        this.services = services;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if (!(sender instanceof Player player)) {
            services.messenger().error(sender, "Rank mannequins can only be spawned by a player.");
            return;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            int removed = services.rankMannequins().clear(player);
            services.messenger().success(player, "Removed <accent><count><success> rank mannequins.",
                    Messenger.value("count", String.valueOf(removed)));
            return;
        }
        if (args.length > 0) {
            services.messenger().error(player, "Usage: <accent>/testranks [clear]");
            return;
        }

        int spawned = services.rankMannequins().spawn(player);
        if (spawned > 0) {
            services.messenger().success(player, "Spawned <accent><count><success> rank mannequins.",
                    Messenger.value("count", String.valueOf(spawned)));
            services.sounds().confirm(player);
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            return "clear".startsWith(prefix) ? List.of("clear") : List.of();
        }
        return List.of();
    }

    @Override
    public String permission() {
        return Permissions.TOOL_TEST_RANKS;
    }
}
