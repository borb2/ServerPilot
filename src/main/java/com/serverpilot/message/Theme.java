package com.serverpilot.message;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class Theme {
    public static final TextColor PRIMARY = color("#2FA36B");
    public static final TextColor ACCENT = color("#7BE8A6");
    public static final TextColor SUCCESS = color("#4ADE80");
    public static final TextColor ERROR = color("#F1626C");
    public static final TextColor WARNING = color("#F2B441");
    public static final TextColor TEXT = color("#E6EAED");
    public static final TextColor MUTED = color("#8A9199");
    public static final TextColor FAINT = color("#5C636B");
    public static final TagResolver TAGS = TagResolver.builder()
            .resolver(styling("primary", PRIMARY))
            .resolver(styling("accent", ACCENT))
            .resolver(styling("success", SUCCESS))
            .resolver(styling("error", ERROR))
            .resolver(styling("warning", WARNING))
            .resolver(styling("text", TEXT))
            .resolver(styling("muted", MUTED))
            .resolver(styling("faint", FAINT))
            .build();
    private static TagResolver styling(String name, TextColor color) {
        return TagResolver.resolver(name, Tag.styling(color));
    }

    private static TextColor color(String hex) {
        TextColor parsed = TextColor.fromHexString(hex);
        if (parsed == null) {
            throw new IllegalStateException("Invalid theme colour: " + hex);
        }
        return parsed;
    }

    private Theme() {
    }
}
