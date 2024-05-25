package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.modifiers.classes.FastComback;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
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

        // Void infinite deaths protection
        if (PlayerDeathListener.deathLocation.get(player).getY() > -62) {
            event.setRespawnLocation(PlayerDeathListener.deathLocation.get(player));
        }

        // else: uses actual respawn location
        respawnLocation.put(player, event.getRespawnLocation());

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.setWalkSpeed(0f);
            // Prevent players from starting flying.
            GameState.flySpeedZero.put(player, true);
            player.setFreezeTicks(player.getMaxFreezeTicks());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 1);

            if (FastComback.active) {
                deathTimer.put(player, 2 * Math.pow(1.07, player.getLevel()));
            } else {
                deathTimer.put(player, 5 * Math.pow(1.07, player.getLevel()));
            }

            deathTimerCountdown(player);

        }, 5L);
    }

    private void deathTimerCountdown(Player player) {
        Timer timer = Main.getInstance().getTimer();

        new BukkitRunnable() {
            @Override
            public void run() {
                // If the game ends before the player respawns
                if (!timer.isRunning()) {
                    player.setWalkSpeed(0.2f);
                    GameState.flySpeedZero.put(player, false);
                    player.setFreezeTicks(0);
                    this.cancel();
                    return;
                }

                if (deathTimer.get(player) <= 0) {
                    // Set default values.
                    player.teleport(respawnLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setWalkSpeed(0.2f);
                    GameState.flySpeedZero.put(player, false);
                    player.setFreezeTicks(0);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 12 * 20, 9));

                    player.resetTitle();
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                    // If the player has a teleporter in the inventory display 'Teleport Available'
                    if (player.getInventory().contains(Material.END_PORTAL_FRAME)
                            || player.getInventory().contains(Material.GILDED_BLACKSTONE)) {
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                            player.sendTitle("", ChatColor.LIGHT_PURPLE
                                    + "ᴛᴇʟᴇᴘᴏʀᴛ ᴀᴠᴀɪʟᴀʙʟᴇ", 0, 60, 20);
                            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                        }, 40L);
                    }

                    this.cancel();
                    return;
                }

                player.sendTitle(HexColor.PURPLE + "Respawning in",
                        ChatColor.WHITE.toString() + ChatColor.BOLD + "» "
                                + String.format("%1.1f", deathTimer.get(player)) + " «",
                        0, 25, 0);
                deathTimer.put(player, deathTimer.get(player) - 0.1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

}
