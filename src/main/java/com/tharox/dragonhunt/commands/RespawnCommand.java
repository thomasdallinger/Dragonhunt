package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
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
        if (checkIfEnoughCoins(player, "/respawn")) return true;

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Coins.removeCoins(player, getCommandCost(Main.getInstance().getConfig(), "/respawn"),
                ChatColor.GOLD.toString() + ChatColor.BOLD + "/respawn");
        player.setBedSpawnLocation(player.getLocation(), true);
        //player.sendMessage(Prefixes.getCoinPrefix() + HexColor.GREEN + "Respawn Point set.");

        return false;
    }
}
