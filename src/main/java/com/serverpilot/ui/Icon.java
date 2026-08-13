package com.serverpilot.ui;

import com.serverpilot.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class Icon {
    private final Messenger messenger;
    private final ItemStack stack;
    private final List<Component> lore = new ArrayList<>();

    private Icon(Messenger messenger, Material material, int amount) {
        this.messenger = messenger;
        this.stack = new ItemStack(material, amount);
    }

    public static Icon of(Messenger messenger, Material material) {
        return new Icon(messenger, material, 1);
    }

    public static Icon of(Messenger messenger, Material material, int amount) {
        return new Icon(messenger, material, Math.clamp(amount, 1, 64));
    }

    public Icon name(String miniMessage, TagResolver... placeholders) {
        stack.editMeta(meta -> meta.displayName(plain(miniMessage, placeholders)));
        return this;
    }

    public Icon lore(String miniMessage, TagResolver... placeholders) {
        lore.add(plain(miniMessage, placeholders));
        return this;
    }

    public Icon blank() {
        lore.add(Component.empty());
        return this;
    }

    public Icon field(String label, String value) {
        return lore("<muted>" + label + ": <text><val>", Messenger.value("val", value));
    }

    public Icon glow() {
        stack.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
        return this;
    }

    public Icon skull(Player player) {
        stack.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(player.getPlayerProfile()));
        return this;
    }

    public Icon tag(NamespacedKey key, String value) {
        stack.editMeta(meta -> meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value));
        return this;
    }

    public ItemStack build() {
        if (!lore.isEmpty()) {
            stack.editMeta(meta -> meta.lore(List.copyOf(lore)));
        }
        return stack;
    }

    private Component plain(String miniMessage, TagResolver... placeholders) {
        return messenger.render(miniMessage, placeholders).decoration(TextDecoration.ITALIC, false);
    }
}
