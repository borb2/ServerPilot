package com.serverpilot.ui.menus;

import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Section;
import com.serverpilot.ui.Services;
import io.papermc.paper.ServerBuildInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class DashboardMenu extends Menu {

    private static final Section[] SECTIONS = {Section.TOOLS, Section.PLUGINS, Section.INTEGRATIONS};
    private static final int[] SLOTS = {13, 30, 32};
    private static final int SLOT_ABOUT = 31;

    public DashboardMenu(Services services) {
        super(services, null);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>ServerPilot");
    }

    @Override
    protected int size() {
        return 36;
    }

    @Override
    protected void build(Player viewer) {
        for (int i = 0; i < SECTIONS.length && i < SLOTS.length; i++) {
            Section section = SECTIONS[i];
            set(SLOTS[i], sectionIcon(viewer, section), (player, click) -> {
                if (!section.allowed(player)) {
                    services.messenger().error(player, "You do not have access to <accent><section><error>.",
                            Messenger.value("section", section.displayName()));
                    services.sounds().deny(player);
                    return;
                }
                section.create(services, this).open(player);
            });
        }
        set(SLOT_ABOUT, about());
    }

    private ItemStack sectionIcon(Player viewer, Section section) {
        boolean allowed = section.allowed(viewer);
        Icon icon = Icon.of(services.messenger(), section.icon())
                .name((allowed ? "<accent>" : "<muted>") + section.displayName())
                .lore("<muted>" + section.description())
                .blank();
        return (allowed
                ? icon.lore("<faint>Click to open")
                : icon.lore("<error>No permission"))
                .build();
    }

    private ItemStack about() {
        ServerBuildInfo build = ServerBuildInfo.buildInfo();
        return Icon.of(services.messenger(), Material.NAME_TAG)
                .name("<primary>ServerPilot <muted>v<version>",
                        Messenger.value("version", services.plugin().getPluginMeta().getVersion()))
                .lore("<faint><brand> <version>",
                        Messenger.value("brand", build.brandName()),
                        Messenger.value("version", build.minecraftVersionName()))
                .build();
    }
}
