package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerRespawnListener implements Listener {

    private HashMap<Player, Double> deathTimer = new HashMap<>();
    private HashMap<Player, Location> respawnLocation = new HashMap<>();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        respawnLocation.put(player, event.getRespawnLocation());
        event.setRespawnLocation(PlayerDeathListener.deathLocation.get(player));

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.setWalkSpeed(0f);
            player.setFlySpeed(0f);
            player.setFreezeTicks(1000);

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 1);

            deathTimer.put(player, ((player.getLevel() + 20) * 1.2));
            deathTimerCountdown(player);

        }, 5L);
    }

    private void deathTimerCountdown(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (deathTimer.get(player) <= 0) {
                    // Set default values.
                    player.teleport(respawnLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    player.setFreezeTicks(0);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 9));

                    player.resetTitle();
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                    this.cancel();
                    return;
                }

                player.sendTitle(HexColor.HIGHLIGHT + "Respawning in",
                        ChatColor.WHITE.toString() + ChatColor.BOLD + "» "
                                + String.format("%1.1f", deathTimer.get(player)) + " «",
                        0, 25, 0);
                deathTimer.put(player, deathTimer.get(player) - 0.1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

}
