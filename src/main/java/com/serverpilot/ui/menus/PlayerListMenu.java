package com.serverpilot.ui.menus;

import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.PaginatedMenu;
import com.serverpilot.ui.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public final class PlayerListMenu extends PaginatedMenu<Player> {

    public PlayerListMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Players");
    }

    @Override
    protected List<Player> entries() {
        return services.plugin().getServer().getOnlinePlayers().stream()
                .map(Player.class::cast)
                .sorted(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    protected ItemStack icon(Player entry) {
        return Icon.of(services.messenger(), Material.PLAYER_HEAD)
                .skull(entry)
                .name("<accent>" + entry.getName())
                .blank()
                .lore("<faint>Click for details")
                .build();
    }

    @Override
    protected void onSelect(Player viewer, Player entry) {
        if (!entry.isOnline()) {
            services.messenger().error(viewer, "That player is no longer online.");
            services.sounds().deny(viewer);
            refresh(viewer);
            return;
        }
        new PlayerInfoMenu(services, this, entry.getUniqueId(), entry.getName()).open(viewer);
    }

    @Override
    protected ItemStack emptyIcon() {
        return Icon.of(services.messenger(), Material.BARRIER)
                .name("<muted>Nobody online")
                .lore("<faint>Players appear here as they join")
                .build();
    }
}
