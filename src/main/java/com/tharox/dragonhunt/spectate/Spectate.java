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
    private static final double duration = 10;
    public double timeLeft = duration;

    public Spectate(Player player) {
        activateSpectate(player);
    }

    public static double getDuration() {
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
                    GameState.kitAbilityActive.put(player, false);
                    player.teleport(Spectate.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    cancel();
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new ComponentBuilder("DURATION").color(HexColor.YELLOW).bold(true)
                                .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                .append(String.format("%1.1f", timeLeft) + " seconds").color(ChatColor.GRAY)
                                .create());

                if (timeLeft % 1 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                }

                timeLeft -= 0.1;
            }

        }.runTaskTimer(Main.getInstance(), 2, 2);
    }
}
