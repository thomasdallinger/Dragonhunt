package com.tharox.dragonhunt.scout;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Scout {
    public static final int COOLDOWN = 120;
    // Used to determine if a scout is currently active, too.
    public static HashMap<Player, Integer> scoutCooldown = new HashMap<>();
    // Location the player started the scout from.
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    private static final int duration = 30;
    private int timeLeft = duration;
    Timer timer = Main.getInstance().getTimer();

    public Scout(Player player) {
        activateSpectate(player);
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
                if (timeLeft <= 0 || scoutCooldown.get(player) >= timer.getTime()) {
                    player.teleport(Scout.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(HexColor.RED + "Scout ended.");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                    scoutCooldown.put(player, timer.getTime() + Scout.COOLDOWN);
                    player.setCooldown(Material.EMERALD, Scout.COOLDOWN * 20);
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
