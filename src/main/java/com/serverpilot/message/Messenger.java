package com.serverpilot.message;

import com.serverpilot.config.ServerPilotConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class Messenger {
    private final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(TagResolver.standard())
                    .resolver(Theme.TAGS)
                    .build())
            .build();
    private final ServerPilotConfig config;
    public Messenger(ServerPilotConfig config) {
        this.config = config;
    }

    public Component render(String message, TagResolver... placeholders) {
        return miniMessage.deserialize(message, placeholders);
    }

    public static TagResolver value(String name, String raw) {
        return Placeholder.unparsed(name, raw);
    }

    public static TagResolver styled(String name, Component component) {
        return Placeholder.component(name, component);
    }

    public Component prefix() {
        return config.showPrefix() ? render(config.prefix()) : Component.empty();
    }

    public void send(Audience audience, String message, TagResolver... placeholders) {
        audience.sendMessage(prefix().append(render(message, placeholders)));
    }

    public void sendRaw(Audience audience, String message, TagResolver... placeholders) {
        audience.sendMessage(render(message, placeholders));
    }

    public void success(Audience audience, String message, TagResolver... placeholders) {
        send(audience, "<success>✔ " + message, placeholders);
    }

    public void error(Audience audience, String message, TagResolver... placeholders) {
        send(audience, "<error>✖ " + message, placeholders);
    }

    public void warning(Audience audience, String message, TagResolver... placeholders) {
        send(audience, "<warning>⚠ " + message, placeholders);
    }

    public void info(Audience audience, String message, TagResolver... placeholders) {
        send(audience, "<text>" + message, placeholders);
    }
}
