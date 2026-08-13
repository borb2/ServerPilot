package com.serverpilot.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PaginatedMenu<T> extends Menu {
    private static final int SLOT_PREVIOUS = 48;
    private static final int SLOT_NEXT = 50;

    private int page;
    protected PaginatedMenu(Services services, Menu parent) {
        super(services, parent);
    }

    protected abstract List<T> entries();

    protected abstract ItemStack icon(T entry);
    protected abstract void onSelect(Player viewer, T entry);

    protected abstract ItemStack emptyIcon();

    @Override
    protected final int size() {
        return 54;
    }

    @Override
    protected final void build(Player viewer) {
        List<T> all = entries();
        page = Pagination.clampPage(page, all.size());
        if (all.isEmpty()) {
            set(22, emptyIcon());
            return;
        }
        List<T> visible = Pagination.page(all, page);
        for (int i = 0; i < visible.size(); i++) {
            T entry = visible.get(i);
            set(Pagination.CONTENT_SLOTS[i], icon(entry), (player, click) -> onSelect(player, entry));
        }
        int pages = Pagination.pageCount(all.size());
        if (page > 0) {
            set(SLOT_PREVIOUS, pageIcon("Previous page", page, pages), (player, click) -> {
                page--;
                refresh(player);
            });
        }
        if (page < pages - 1) {
            set(SLOT_NEXT, pageIcon("Next page", page + 2, pages), (player, click) -> {
                page++;
                refresh(player);
            });
        }
    }

    private ItemStack pageIcon(String label, int target, int pages) {
        return Icon.of(services.messenger(), Material.PAPER)
                .name("<accent>" + label)
                .lore("<muted>Page <text>" + target + "<muted> of <text>" + pages)
                .build();
    }
}
