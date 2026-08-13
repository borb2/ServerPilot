package com.serverpilot.tool;

import com.serverpilot.Permissions;
import com.serverpilot.ui.Services;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class DebugWandListener implements Listener {

    private final Services services;

    public DebugWandListener(Services services) {
        this.services = services;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !services.debugWand().isWand(event.getItem())) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        event.setCancelled(true);
        if (!allowed(event.getPlayer())) {
            return;
        }
        services.debugWand().inspect(event.getPlayer(), block);
        services.sounds().inspect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(PrePlayerAttackEntityEvent event) {
        if (!services.debugWand().isWand(event.getPlayer().getInventory().getItemInMainHand())) {
            return;
        }
        event.setCancelled(true);
        inspect(event.getPlayer(), event.getAttacked());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !services.debugWand().isWand(
                event.getPlayer().getInventory().getItemInMainHand())) {
            return;
        }
        event.setCancelled(true);
        inspect(event.getPlayer(), event.getRightClicked());
    }

    private void inspect(Player player, Entity target) {
        if (!allowed(player)) {
            return;
        }
        services.debugWand().inspect(player, target);
        services.sounds().inspect(player);
    }

    private boolean allowed(Player player) {
        if (!services.config().debugWandEnabled()) {
            services.messenger().error(player, "The debug wand is disabled in the ServerPilot config.");
            return false;
        }
        if (!player.hasPermission(Permissions.TOOL_DEBUG_WAND)) {
            services.messenger().error(player, "You do not have permission to use the debug wand.");
            services.sounds().deny(player);
            return false;
        }
        return true;
    }
}
