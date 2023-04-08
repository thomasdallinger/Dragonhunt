package com.tharox.dragonhunt.spectate;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.classes.Kit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Spectate {
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    private static final int duration = 10;
    public int timeLeft = duration;

    public Spectate(Player player) {
        activateSpectate(player);
    }

    public static int getDuration() {
        return duration;
    }

    private void activateSpectate(Player player) {
        spectateLocation.put(player, player.getLocation());

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.setGameMode(GameMode.SPECTATOR);

        spectateDurationLeft(player);
    }

    private void spectateDurationLeft(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    player.teleport(Spectate.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    cancel();
                }

                timeLeft -= 1;
                GameState.kitAbilityActive.put(player, timeLeft);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);

            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}
