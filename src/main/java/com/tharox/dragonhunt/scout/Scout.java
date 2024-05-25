package com.tharox.dragonhunt.scout;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Scout {
    public static final int COOLDOWN = 10; // Seconds
    public static final int USAGES = 5;
    public static HashMap<Player, Integer> scoutCooldown = new HashMap<>();
    public static HashMap<Player, Integer> scoutUsages = new HashMap<>();
    // Location the player started the scout from.
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    private static final int duration = 20;
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
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(HexColor.RED + "Scout ended.");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                    scoutCooldown.put(player, timer.getTime() + Scout.COOLDOWN);
                    player.setCooldown(Material.ENDER_EYE, Scout.COOLDOWN * 20);

                    // Teleport the player if possible
                    Location location = player.getLocation();
                    if (!location.getWorld().equals(Scout.spectateLocation.get(player).getWorld())) {
                        player.sendMessage(Prefixes.getErrorPrefix()
                                + "You cannot be teleported between dimensions.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

                        player.teleport(Scout.spectateLocation.get(player));

                    } else if (isOutsideWorldBorder(player)) {
                        player.sendMessage(Prefixes.getErrorPrefix()
                                + "You cannot be teleported outside the world border.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

                        player.teleport(Scout.spectateLocation.get(player));

                    } else {
                        player.teleport(location);
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.WEAKNESS, 10 * 20, 2));
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.SLOW, 10 * 20, 1));

                        player.sendTitle("", HexColor.GREEN + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ",
                                0, 30, 10);
                        player.sendMessage(HexColor.GREEN + "Successfully teleported to your scouted location.");
                        player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED + "Weakness 3 "
                                + ChatColor.GRAY + "applied.");
                        player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED + "Slowness 2 "
                                + ChatColor.GRAY + "applied.");
                    }

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

    private boolean isOutsideWorldBorder(Player player) {
        World world = player.getWorld();
        WorldBorder border = world.getWorldBorder();
        Location location = player.getLocation();

        double borderSize = border.getSize() / 2; // WorldBorder size is the full diameter
        Location center = border.getCenter();

        double minX = center.getX() - borderSize;
        double maxX = center.getX() + borderSize;
        double minZ = center.getZ() - borderSize;
        double maxZ = center.getZ() + borderSize;

        double playerX = location.getX();
        double playerZ = location.getZ();

        return playerX < minX || playerX > maxX || playerZ < minZ || playerZ > maxZ;
    }
}
