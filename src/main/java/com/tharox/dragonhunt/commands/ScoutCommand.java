package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.scout.Scout;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ScoutCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Timer timer = Main.getInstance().getTimer();
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return false;

        if (!timer.isRunning()) {
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        // Check if the player has an active Scout
        if (Scout.spectateLocation.containsKey(player)) {
            Location location = player.getLocation();

            if (args.length == 0) {
                // Cancel the currently active scout
                Scout.scoutCooldown.put(player, timer.getTime() + Scout.COOLDOWN);
                player.setCooldown(Material.ENDER_EYE, Scout.COOLDOWN * 20);
                return true;
            }

            // Teleport the player
            if (args[0].equalsIgnoreCase("tp")) {
                if (GameState.selectedKit.containsKey(player)) {
                    if (!location.getWorld().equals(Scout.spectateLocation.get(player).getWorld())) {
                        player.sendMessage(Prefixes.getErrorPrefix()
                                + "You cannot be teleported between dimensions.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                        return false;

                    } else {
                        // Cancel the currently active scout
                        Scout.scoutCooldown.put(player, timer.getTime() + Scout.COOLDOWN);
                        player.setCooldown(Material.ENDER_EYE, Scout.COOLDOWN * 20);

                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
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

                            for (Player online : Bukkit.getOnlinePlayers()) {
                                online.sendMessage(Prefixes.getImportantPrefix()
                                        + ChatColor.WHITE + player.getName() + ChatColor.GRAY + " teleported using "
                                        + HexColor.YELLOW + "/scout tp" + ChatColor.GRAY + ".");
                            }
                        }, 20L);

                        return true;
                    }
                }
            }
        } else {
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                player.sendMessage(Prefixes.getErrorPrefix() + "You cannot scout while in Spectator Mode.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                return false;
            }
        }

        if (Scout.scoutCooldown.get(player) >= timer.getTime()) {
            player.sendMessage(Prefixes.getErrorPrefix() + "Scout is on cooldown for "
                    + HexColor.RED + (player.getCooldown(Material.ENDER_EYE) / 20)
                    + ChatColor.RED + " seconds.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        // Player has to have more than 5 hearts.
        if (player.getHealth() < 10.0) {
            player.sendMessage(Prefixes.getErrorPrefix() + "You need to have more than 5 ❤ " +
                    "to be able to scout.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        new Scout(player);

        player.sendMessage(HexColor.GREEN + "Scout activated.");
        player.sendMessage(ChatColor.GRAY + "Cancel the Scout using "
                + ChatColor.WHITE + "/scout" + ChatColor.GRAY + " again.");
        player.sendMessage(ChatColor.GRAY + "Teleport to your desired location using "
                + ChatColor.WHITE + "/scout tp" + ChatColor.GRAY + ".");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GRAY
                    + " uses " + HexColor.YELLOW + "/scout" + ChatColor.GRAY + ".");
        }

        return true;
    }
}
