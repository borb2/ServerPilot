package com.serverpilot.tool;

import com.serverpilot.Keys;
import com.serverpilot.config.ServerPilotConfig;
import com.serverpilot.message.Messenger;
import com.serverpilot.ui.Icon;
import com.serverpilot.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class DebugWand {
    public static final String ID = "debug_wand";
    private final Keys keys;
    private final ServerPilotConfig config;
    private final Messenger messenger;
    public DebugWand(Keys keys, ServerPilotConfig config, Messenger messenger) {
        this.keys = keys;
        this.config = config;
        this.messenger = messenger;
    }

    public ItemStack create() {
        return Icon.of(messenger, config.debugWandMaterial())
                .name("<accent>Debug Wand")
                .lore("<muted>Inspect blocks and entities")
                .blank()
                .lore("<faint>Click a block <muted>· <text>block report")
                .lore("<faint>Click an entity <muted>· <text>entity report")
                .glow()
                .tag(keys.tool, ID)
                .build();
    }

    public boolean isWand(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        return ID.equals(item.getPersistentDataContainer().get(keys.tool, PersistentDataType.STRING));
    }

    public void inspect(Player viewer, Block block) {
        header(viewer, "Block");
        line(viewer, "Material", Text.prettify(block.getType().name()));
        line(viewer, "World", block.getWorld().getName());
        line(viewer, "Position", Text.blockCoordinates(block.getX(), block.getY(), block.getZ()));
        line(viewer, "Chunk", block.getChunk().getX() + ", " + block.getChunk().getZ());
        line(viewer, "Biome", block.getBiome().getKey().asString());
        line(viewer, "Light", block.getLightLevel()
                + " (sky " + block.getLightFromSky() + ", block " + block.getLightFromBlocks() + ")");
        line(viewer, "Data", Text.truncate(block.getBlockData().getAsString(false), 120));
    }

    public void inspect(Player viewer, Entity entity) {
        header(viewer, "Entity");
        line(viewer, "Type", Text.prettify(entity.getType().name()));
        line(viewer, "UUID", entity.getUniqueId().toString());
        line(viewer, "World", entity.getWorld().getName());
        line(viewer, "Position", Text.coordinates(
                entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()));
        Component customName = entity.customName();
        if (customName != null) {
            line(viewer, "Name", PlainTextComponentSerializer.plainText().serialize(customName));
        }
        if (entity instanceof LivingEntity living) {
            line(viewer, "Health", Text.decimal(living.getHealth(), 1) + " / " + Text.decimal(maxHealth(living), 1));
        }
        if (!entity.getScoreboardTags().isEmpty()) {
            line(viewer, "Tags", Text.truncate(String.join(", ", entity.getScoreboardTags()), 100));
        }
    }

    private double maxHealth(LivingEntity living) {
        AttributeInstance attribute = living.getAttribute(Attribute.MAX_HEALTH);
        return attribute == null ? living.getHealth() : attribute.getValue();
    }

    private void header(Player viewer, String subject) {
        messenger.sendRaw(viewer, "");
        messenger.sendRaw(viewer, "<primary>▍ <accent>" + subject + " inspection");
    }

    private void line(Player viewer, String label, String value) {
        messenger.sendRaw(viewer, "<faint>  " + label + " <muted>· <text><value>", Messenger.value("value", value));
    }
}
