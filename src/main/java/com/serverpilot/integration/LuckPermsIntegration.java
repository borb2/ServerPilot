package com.serverpilot.integration;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;

public final class LuckPermsIntegration implements Integration {

    private LuckPerms luckPerms;

    @Override
    public String displayName() {
        return "LuckPerms";
    }

    @Override
    public String pluginName() {
        return "LuckPerms";
    }

    @Override
    public void enable(JavaPlugin plugin) {
        luckPerms = LuckPermsProvider.get();
    }

    @Override
    public void disable() {
        luckPerms = null;
    }

    public List<Group> groupsByWeight() {
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .sorted(Comparator.comparingInt((Group group) -> group.getWeight().orElse(0)).reversed()
                        .thenComparing(Group::getName))
                .toList();
    }
}
