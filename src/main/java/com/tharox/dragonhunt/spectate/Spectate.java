package com.tharox.dragonhunt.spectate;

import com.tharox.dragonhunt.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Spectate {
    public static HashMap<Player, Integer> spectatesLeft = new HashMap<>();
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    public static HashMap<Player, Boolean> commandUsable = new HashMap<>();

    public Spectate(Player player) {
        activateSpectate(player);
    }

    private void activateSpectate(Player player) {
        final int[] countdown = {4};
        List<PotionEffect> effects = new ArrayList();
        effects.add(new PotionEffect(PotionEffectType.SLOW, 40, 9));
        effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 40, 9));
        effects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 9));
        effects.add(new PotionEffect(PotionEffectType.JUMP, 40, -9));

        commandUsable.put(player, false);
        spectatesLeft.put(player, spectatesLeft.get(player) - 1);
        spectateLocation.put(player, player.getLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1, 1);

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.getPrefix() + ChatColor.GOLD + ChatColor.BOLD + player.getName().toUpperCase()
                    + ChatColor.YELLOW + " uses a Spectate Token.");
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (countdown[0] <= 0) {
                    player.setGameMode(GameMode.SPECTATOR);
                    spectateDurationLeft(player);
                    cancel();
                    return;
                }

                player.spawnParticle(Particle.PORTAL, player.getLocation(), 50);
                player.sendMessage(ChatColor.YELLOW + "Spectate starts in " + ChatColor.GOLD + countdown[0]
                        + ChatColor.YELLOW + " seconds.");
                player.addPotionEffects(effects);
                countdown[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    private void spectateDurationLeft(Player player) {
        final int[] duration = {15};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (duration[0] <= 0) {
                    player.teleport(Spectate.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    commandUsable.put(player, true);
                    cancel();
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString()
                        + ChatColor.BOLD + duration[0] + ChatColor.YELLOW + ChatColor.BOLD + " SECONDS LEFT"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                duration[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}
