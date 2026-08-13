package com.serverpilot.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {
    @FunctionalInterface
    public interface ClickHandler {
        void handle(Player player, ClickType click);
    }

    protected final Services services;
    private final Menu parent;
    private final Map<Integer, ClickHandler> handlers = new HashMap<>();
    private Inventory inventory;

    protected Menu(Services services, Menu parent) {
        this.services = services;
        this.parent = parent;
    }

    protected abstract Component title();
    protected abstract int size();

    protected abstract void build(Player viewer);

    public final void open(Player viewer) {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, size(), title());
        }
        render(viewer);
        viewer.openInventory(inventory);
        services.sounds().open(viewer);
    }

    public final void refresh(Player viewer) {
        if (inventory == null) {
            open(viewer);
            return;
        }
        render(viewer);
    }

    private void render(Player viewer) {
        handlers.clear();
        inventory.clear();
        build(viewer);
        if (parent != null) {
            set(size() - 5, Icon.of(services.messenger(), Material.ARROW)
                    .name("<accent>Back")
                    .lore("<muted>Return to the previous menu")
                    .build(), (player, click) -> parent.open(player));
        }
        if (services.config().fillEmptySlots()) {
            fillEmpty();
        }
    }

    protected final void set(int slot, ItemStack item) {
        set(slot, item, null);
    }

    protected final void set(int slot, ItemStack item, ClickHandler handler) {
        if (slot < 0 || slot >= size()) {
            throw new IllegalArgumentException("Slot " + slot + " outside menu of size " + size());
        }
        inventory.setItem(slot, item);
        if (handler != null) {
            handlers.put(slot, handler);
        }
    }

    protected final ItemStack plannedIcon(Material material, String name, String description) {
        return Icon.of(services.messenger(), material)
                .name("<muted>" + name)
                .lore("<faint>" + description)
                .blank()
                .lore("<error>Not implemented yet")
                .build();
    }

    private void fillEmpty() {
        ItemStack filler = Icon.of(services.messenger(), Material.GRAY_STAINED_GLASS_PANE)
                .name("<faint>")
                .build();
        for (int slot = 0; slot < size(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }

    final void click(Player player, int slot, ClickType type) {
        ClickHandler handler = handlers.get(slot);
        if (handler == null) {
            return;
        }
        try {
            handler.handle(player, type);
        } catch (RuntimeException e) {
            services.messenger().error(player, "Something went wrong handling that click.");
            services.sounds().deny(player);
            services.plugin().getSLF4JLogger().error(
                    "Menu {} failed handling slot {} for {}", getClass().getSimpleName(), slot, player.getName(), e);
        }
    }

    @Override
    public final Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, size(), title());
        }
        return inventory;
    }
}
