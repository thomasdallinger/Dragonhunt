package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PaidCommandsValidator {
    static Timer timer = Main.getInstance().getTimer();

    public static boolean commandExecutionIsValid(Player player, int cost) {
        if (!timer.isRunning()) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        if (Coins.getCoins(player) < cost) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You don't have enough coins! Need "
                    + HexColor.RED + (cost - Coins.getCoins(player))
                    + ChatColor.GRAY + " more.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return false;
        }

        return true;
    }
}
