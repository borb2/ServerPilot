package com.serverpilot.ui.menus;

import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Icon;
import com.serverpilot.ui.Menu;
import com.serverpilot.ui.Services;
import com.serverpilot.util.Text;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public final class PluginInfoMenu extends Menu {

    private final String pluginName;

    public PluginInfoMenu(Services services, Menu parent, String pluginName) {
        super(services, parent);
        this.pluginName = pluginName;
    }

    @Override
    protected Component title() {
        return services.messenger().render("<primary>Plugin <muted>· <text><name>",
                Messenger.value("name", Text.truncate(pluginName, 24)));
    }

    @Override
    protected int size() {
        return 45;
    }

    @Override
    protected void build(Player viewer) {
        Plugin target = services.plugin().getServer().getPluginManager().getPlugin(pluginName);
        if (target == null) {
            set(22, Icon.of(services.messenger(), Material.BARRIER)
                    .name("<error>Plugin unavailable")
                    .lore("<muted><name> is no longer loaded.", Messenger.value("name", pluginName))
                    .build());
            return;
        }

        PluginMeta meta = target.getPluginMeta();
        boolean enabled = target.isEnabled();

        set(13, Icon.of(services.messenger(), enabled ? Material.ENCHANTED_BOOK : Material.BOOK)
                .name((enabled ? "<accent>" : "<muted>") + meta.getName())
                .lore("<muted>State: " + (enabled ? "<success>Enabled" : "<error>Disabled"))
                .build());

        Icon identity = Icon.of(services.messenger(), Material.NAME_TAG)
                .name("<accent>Identity")
                .field("Version", meta.getVersion());
        if (meta.getAPIVersion() != null) {
            identity.field("API version", meta.getAPIVersion());
        }
        if (!meta.getAuthors().isEmpty()) {
            identity.field("Authors", Text.truncate(String.join(", ", meta.getAuthors()), 48));
        }
        if (meta.getWebsite() != null && !meta.getWebsite().isBlank()) {
            identity.field("Website", Text.truncate(meta.getWebsite(), 48));
        }
        set(20, identity.build());

        Icon description = Icon.of(services.messenger(), Material.WRITABLE_BOOK).name("<accent>Description");
        if (meta.getDescription() == null || meta.getDescription().isBlank()) {
            description.lore("<faint>None provided");
        } else {
            for (String line : Text.wrap(meta.getDescription(), 40, 5)) {
                description.lore("<text><line>", Messenger.value("line", line));
            }
        }
        set(22, description.build());

        Icon dependencies = Icon.of(services.messenger(), Material.IRON_CHAIN).name("<accent>Dependencies");
        addList(dependencies, "Required", meta.getPluginDependencies());
        addList(dependencies, "Optional", meta.getPluginSoftDependencies());
        addList(dependencies, "Provides", meta.getProvidedPlugins());
        set(24, dependencies.build());
    }

    private void addList(Icon icon, String label, List<String> values) {
        icon.field(label, values.isEmpty() ? "none" : Text.truncate(String.join(", ", values), 48));
    }
}
