package com.tharox.dragonhunt.scout;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Scout {
    public static HashMap<Player, Boolean> scoutAvailable = new HashMap<>();
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    private static final int duration = 25;
    public int timeLeft = duration;

    public Scout(Player player) {
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
                if (timeLeft <= 0 || !scoutAvailable.get(player)) {
                    player.teleport(Scout.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(HexColor.RED + "Scout ended.");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                    scoutAvailable.put(player, false);
                    spectateLocation.remove(player);

                    cancel();
                    return;
                }

                timeLeft -= 1;

                // Displaying the duration left in the action bar
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new ComponentBuilder("DURATION").color(HexColor.YELLOW).bold(true)
                                .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                .append(timeLeft + " seconds")
                                .color(ChatColor.GRAY).create());

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);

            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}
