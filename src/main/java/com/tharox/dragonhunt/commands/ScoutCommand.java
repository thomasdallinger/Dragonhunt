package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.scout.Scout;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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

        if (Scout.spectateLocation.containsKey(player)) {
            // Cancel the currently active scout
            Scout.scoutAvailable.put(player, false);
            return true;
        }

        if (!Scout.scoutAvailable.get(player)) {
            player.sendMessage(Prefixes.getErrorPrefix() + "You do not have a Scout available.");
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

        // Player loses 5 hearts before being put into Spectator Mode.
        player.setHealth(player.getHealth() - 10.0);
        new Scout(player);

        player.sendMessage(HexColor.GREEN + "Scout active.");
        player.sendMessage(ChatColor.GRAY + "Cancel the Scout using /scout again.");
        player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED + "-5 ❤ Penalty");

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GRAY
                    + " uses " + HexColor.YELLOW + "/scout" + ChatColor.GRAY + ".");
        }

        return true;
    }
}
