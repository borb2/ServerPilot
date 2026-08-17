package com.serverpilot.tool;

import com.serverpilot.Keys;
import com.serverpilot.Permissions;
import com.serverpilot.config.ServerPilotConfig;
import com.serverpilot.integration.IntegrationManager;
import com.serverpilot.integration.LuckPermsIntegration;
import com.serverpilot.message.Messenger;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.group.Group;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;

public final class RankMannequins implements Listener {

    private static final double DISTANCE_IN_FRONT = 3.0;

    private final Keys keys;
    private final ServerPilotConfig config;
    private final Messenger messenger;
    private final IntegrationManager integrations;

    public RankMannequins(Keys keys, ServerPilotConfig config, Messenger messenger, IntegrationManager integrations) {
        this.keys = keys;
        this.config = config;
        this.messenger = messenger;
        this.integrations = integrations;
    }

    public int spawn(Player player) {
        LuckPermsIntegration luckPerms = integrations.active(LuckPermsIntegration.class).orElse(null);
        if (luckPerms == null) {
            messenger.error(player, "LuckPerms is not available, so there are no ranks to preview.");
            return 0;
        }

        List<Group> groups = luckPerms.groupsByWeight();
        if (groups.isEmpty()) {
            messenger.warning(player, "LuckPerms reported no groups.");
            return 0;
        }

        clear(player);

        Location eye = player.getLocation();
        Vector forward = eye.getDirection().setY(0).normalize();
        Vector right = new Vector(-forward.getZ(), 0, forward.getX());
        Location start = eye.clone().add(forward.clone().multiply(DISTANCE_IN_FRONT));

        double spacing = config.testRanksSpacing();
        ResolvableProfile profile = ResolvableProfile.resolvableProfile(player.getPlayerProfile());
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            Location spot = start.clone().add(right.clone().multiply(offsetFor(i, groups.size(), spacing)));
            spot.setYaw(eye.getYaw() + 180.0f);
            spot.setPitch(0.0f);

            player.getWorld().spawn(spot, Mannequin.class, mannequin -> {
                mannequin.setProfile(profile);
                mannequin.setImmovable(true);
                mannequin.customName(nameTag(group, player.getName()));
                mannequin.setCustomNameVisible(true);
                mannequin.setPersistent(false);
                mannequin.getPersistentDataContainer()
                        .set(keys.rankMannequin, PersistentDataType.STRING, group.getName());
            });
        }
        return groups.size();
    }

    static double offsetFor(int index, int count, double spacing) {
        return spacing * (index - (count - 1) / 2.0);
    }

    public int clear(Player player) {
        int removed = 0;
        for (Entity entity : player.getWorld().getEntitiesByClass(Mannequin.class)) {
            if (isRankMannequin(entity)) {
                entity.remove();
                removed++;
            }
        }
        return removed;
    }

    private boolean isRankMannequin(Entity entity) {
        return entity.getPersistentDataContainer().has(keys.rankMannequin, PersistentDataType.STRING);
    }

    private Component nameTag(Group group, String playerName) {
        return legacy(prefix(group) + playerName + suffix(group));
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !isRankMannequin(event.getRightClicked())) {
            return;
        }
        event.setCancelled(true);
        handleClick(event.getPlayer(), event.getRightClicked());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeftClick(PrePlayerAttackEntityEvent event) {
        if (!isRankMannequin(event.getAttacked())) {
            return;
        }
        event.setCancelled(true);
        handleClick(event.getPlayer(), event.getAttacked());
    }

    private void handleClick(Player player, Entity mannequin) {
        if (!player.isSneaking()) {
            preview(player, mannequin);
            return;
        }
        if (!player.hasPermission(Permissions.TOOL_TEST_RANKS)) {
            return;
        }
        int removed = clear(player);
        messenger.success(player, "Despawned <accent><count><success> rank mannequins.",
                Messenger.value("count", String.valueOf(removed)));
    }

    private void preview(Player viewer, Entity mannequin) {
        if (!viewer.hasPermission(Permissions.TOOL_TEST_RANKS)) {
            return;
        }
        String groupName = mannequin.getPersistentDataContainer()
                .get(keys.rankMannequin, PersistentDataType.STRING);
        LuckPermsIntegration luckPerms = integrations.active(LuckPermsIntegration.class).orElse(null);
        if (groupName == null || luckPerms == null) {
            return;
        }

        Group group = luckPerms.groupsByWeight().stream()
                .filter(candidate -> candidate.getName().equals(groupName))
                .findFirst()
                .orElse(null);
        if (group == null) {
            messenger.error(viewer, "Group <accent><name><error> no longer exists.",
                    Messenger.value("name", groupName));
            return;
        }

        viewer.sendMessage(legacy(prefix(group) + viewer.getName() + suffix(group) + "&r: test message"));
    }

    private String prefix(Group group) {
        String prefix = group.getCachedData().getMetaData().getPrefix();
        return prefix == null ? "" : prefix;
    }

    private String suffix(Group group) {
        String suffix = group.getCachedData().getMetaData().getSuffix();
        return suffix == null ? "" : suffix;
    }

    private Component legacy(String raw) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw.replace('§', '&'));
    }
}
