package com.serverpilot.command;

import com.serverpilot.Permissions;
import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Section;
import com.serverpilot.ui.Services;
import com.serverpilot.ui.menus.DashboardMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class OpenSubcommand implements Subcommand {

    private final Services services;

    public OpenSubcommand(Services services) {
        this.services = services;
    }

    @Override
    public String name() {
        return "open";
    }

    @Override
    public String usage() {
        return "open [section]";
    }

    @Override
    public String description() {
        return "Open the dashboard or one of its sections";
    }

    @Override
    public String permission() {
        return Permissions.USE;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            services.messenger().error(sender, "Menus can only be opened by a player.");
            return;
        }
        DashboardMenu dashboard = new DashboardMenu(services);
        if (args.length == 0) {
            dashboard.open(player);
            return;
        }

        Optional<Section> section = Section.byId(args[0]);
        if (section.isEmpty()) {
            services.messenger().error(sender, "Unknown section <accent><name><error>. Try: <text><list>",
                    Messenger.value("name", args[0]),
                    Messenger.value("list", String.join(", ", Section.ids(sender))));
            return;
        }
        if (!section.get().allowed(player)) {
            services.messenger().error(sender, "You do not have access to <accent><section><error>.",
                    Messenger.value("section", section.get().displayName()));
            return;
        }
        section.get().create(services, dashboard).open(player);
    }

    @Override
    public Collection<String> suggest(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return List.of();
        }
        String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
        return Section.ids(sender).stream().filter(id -> id.startsWith(prefix)).toList();
    }
}
