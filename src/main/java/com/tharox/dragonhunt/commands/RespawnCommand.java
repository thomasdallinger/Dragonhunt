package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RespawnCommand extends CommandHelper implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        Timer timer = Main.getInstance().getTimer();

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (checkIfValid(player, timer)) return true;

        if (Coins.getCoins(player) < getCommandCost(Main.getInstance().getConfig(), "/respawn")) {
            player.sendMessage(ChatColor.RED + "You don't have enough coins! Need "
                    + (getCommandCost(Main.getInstance().getConfig(), "/respawn") - Coins.getCoins(player))
                    + " more.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Coins.removeCoins(player, getCommandCost(Main.getInstance().getConfig(), "/respawn"));
        player.setBedSpawnLocation(player.getLocation(), true);
        player.sendMessage(HexColor.GREEN + "Respawn point set.");

        return false;
    }
}
