package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CommandErrors {
    /**
     * Checks if the command is allowed to be executed or not.
     * @return true if the command can't be executed
     */
    protected boolean checkIfValid(Player player, Timer timer) {
        if (!timer.isRunning()) {
            player.sendMessage(ChatColor.RED + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (player.getWorld().getName().equals("world_the_end")) {
            player.sendMessage(ChatColor.RED + "This feature is disabled in the End.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }
        return false;
    }
}
