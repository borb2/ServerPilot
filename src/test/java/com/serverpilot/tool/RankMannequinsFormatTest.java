package com.serverpilot.tool;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RankMannequinsFormatTest {

    private String asMiniMessage(String raw) {
        return MiniMessage.miniMessage().serialize(RankMannequins.format(raw)).toLowerCase(Locale.ROOT);
    }

    @Test
    void keepsLegacyCodes() {
        assertTrue(asMiniMessage("&cOwner").contains("red"));
        assertTrue(asMiniMessage("§cOwner").contains("red"));
    }

    @Test
    void readsAmpersandHex() {
        assertTrue(asMiniMessage("&#ff8800Owner").contains("#ff8800"));
    }

    @Test
    void readsMiniMessage() {
        assertTrue(asMiniMessage("<#ff8800>Owner").contains("#ff8800"));
        assertTrue(asMiniMessage("<gradient:#ff8800:#0088ff>Owner").contains("#ff8800"));
        assertTrue(asMiniMessage("<bold><red>Owner").contains("bold"));
    }

    @Test
    void mixesBoth() {
        String out = asMiniMessage("<#ff8800>Owner&7 tag");
        assertTrue(out.contains("#ff8800"));
        assertTrue(out.contains("gray"));
    }
}
