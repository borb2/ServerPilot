package com.serverpilot.command;

import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public interface Subcommand {

    String name();

    String usage();

    String description();

    String permission();

    void execute(CommandSender sender, String[] args);

    default Collection<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
