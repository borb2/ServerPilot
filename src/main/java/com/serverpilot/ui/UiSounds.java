package com.serverpilot.ui;

import com.serverpilot.config.ServerPilotConfig;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public final class UiSounds {
    private final ServerPilotConfig config;
    public UiSounds(ServerPilotConfig config) {
        this.config = config;
    }

    public void open(Player player) {
        play(player, Sound.BLOCK_BARREL_OPEN, 0.5f, 1.4f);
    }

    public void confirm(Player player) {
        play(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.6f);
    }

    public void deny(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.8f);
    }

    public void inspect(Player player) {
        play(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.35f, 1.6f);
    }

    private void play(Player player, Sound sound, float volume, float pitch) {
        if (config.sounds()) {
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
        }
    }
}
