package com.serverpilot.ui.menus;

import com.serverpilot.integration.Integration;
import com.serverpilot.integration.IntegrationStatus;
import com.serverpilot.integration.PlannedIntegration;
import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.PaginatedMenu;
import com.serverpilot.ui.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class IntegrationsMenu extends PaginatedMenu<Integration> {

    public IntegrationsMenu(Services services, Menu parent) {
        super(services, parent);
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Integrations");
    }

    @Override
    protected List<Integration> entries() {
        return services.integrations().roster();
    }

    @Override
    protected ItemStack icon(Integration entry) {
        IntegrationStatus status = services.integrations().status(entry);
        Icon icon = Icon.of(services.messenger(), material(status))
                .name(nameColour(status) + entry.displayName())
                .lore("<muted>Status: " + statusColour(status) + status.label());

        if (entry instanceof PlannedIntegration planned) {
            icon.lore("<faint>" + planned.summary());
        }
        services.integrations().version(entry)
                .ifPresent(version -> icon.field("Installed version", version));
        return icon.build();
    }

    @Override
    protected void onSelect(Player viewer, Integration entry) {
        IntegrationStatus status = services.integrations().status(entry);
        if (status == IntegrationStatus.PLANNED) {
            services.messenger().warning(viewer,
                    "<accent><name><warning> is installed, but its adapter is not written yet.",
                    Messenger.value("name", entry.displayName()));
            return;
        }
        services.messenger().info(viewer, "<accent><name><text> · <status>",
                Messenger.value("name", entry.displayName()),
                Messenger.value("status", status.label()));
    }

    @Override
    protected ItemStack emptyIcon() {
        return Icon.of(services.messenger(), Material.BARRIER)
                .name("<muted>No integrations registered")
                .build();
    }

    private Material material(IntegrationStatus status) {
        return switch (status) {
            case ACTIVE -> Material.LIME_DYE;
            case INSTALLED_INACTIVE, PLANNED -> Material.YELLOW_DYE;
            case FAILED -> Material.RED_DYE;
            case DISABLED, NOT_INSTALLED -> Material.GRAY_DYE;
        };
    }

    private String nameColour(IntegrationStatus status) {
        return status == IntegrationStatus.ACTIVE ? "<accent>" : "<muted>";
    }

    private String statusColour(IntegrationStatus status) {
        return switch (status) {
            case ACTIVE -> "<success>";
            case INSTALLED_INACTIVE, PLANNED -> "<warning>";
            case FAILED -> "<error>";
            case DISABLED, NOT_INSTALLED -> "<faint>";
        };
    }
}
