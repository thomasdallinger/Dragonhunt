package com.tharox.dragonhunt.spectate;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Statement;
import java.util.HashMap;

public class Spectate {
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    public static final double[] duration = {20};

    public Spectate(Player player) {
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
                if (duration[0] <= 0) {
                    GameState.actionBarNeededForKitAbility.put(player, false);
                    player.teleport(Spectate.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    cancel();
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        ChatColor.GOLD.toString() + ChatColor.BOLD + String.format("%1.1f", duration[0])
                                + ChatColor.YELLOW + ChatColor.BOLD + " SECONDS LEFT"));

                if (duration[0] % 1 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                }

                duration[0] -= 0.1;
            }

        }.runTaskTimer(Main.getInstance(), 2, 2);
    }
}
