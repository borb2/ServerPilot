package com.serverpilot.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public final class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        if (!(holderOf(event.getInventory()) instanceof Menu menu)) {
            return;
        }
        event.setCancelled(true);

        if (event.getClickedInventory() != event.getInventory()) {
            return;
        }
        if (event.getWhoClicked() instanceof Player player) {
            menu.click(player, event.getRawSlot(), event.getClick());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {
        if (holderOf(event.getInventory()) instanceof Menu) {
            event.setCancelled(true);
        }
    }

    private InventoryHolder holderOf(org.bukkit.inventory.Inventory inventory) {
        return inventory == null ? null : inventory.getHolder();
    }
}
