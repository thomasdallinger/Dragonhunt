package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.scout.Scout;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            // Cancel the currently active scout
            Scout.scoutCooldown.put(player, timer.getTime() + Scout.COOLDOWN);
            player.setCooldown(Material.ENDER_EYE, Scout.COOLDOWN * 20);
            return true;
        } else {
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                player.sendMessage(Prefixes.getErrorPrefix() + "You cannot scout while in Spectator Mode.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                return false;
            }
        }

        if (Scout.scoutUsages.get(player) == 0) {
            player.sendMessage(Prefixes.getErrorPrefix() + "You have no Scout usages left.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
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
        Scout.scoutUsages.put(player, Scout.scoutUsages.get(player) - 1);
        player.getInventory().setItem(7, GameState.getScoutItem(player));

        player.sendMessage(HexColor.GREEN + "Scout activated.");
        player.sendMessage(ChatColor.GRAY + "Cancel the Scout using "
                + ChatColor.WHITE + "/scout" + ChatColor.GRAY + " again.");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GRAY
                    + " uses " + HexColor.YELLOW + "/scout" + ChatColor.GRAY + "."
                    + ChatColor.DARK_GRAY + " (" + Scout.scoutUsages.get(player) + " Usages left)");
        }

        return true;
    }
}
