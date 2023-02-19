package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RespawnCommand extends PaidCommandsValidator implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int cost = 7;
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (!PaidCommandsValidator.commandExecutionIsValid(player, cost)) return true;

        if (player.getWorld().getName().equals("world_the_end")) {
            player.sendMessage(Prefixes.getErrorPrefix()
                    + ChatColor.GRAY + "Respawn point cannot be set in the End dimension.");
            return true;
        }

        Coins.removeCoins(player, cost, "/respawn");
        player.setBedSpawnLocation(player.getLocation(), true);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.sendMessage(Prefixes.getAllPrefix() + HexColor.GREEN + "Respawn Point set.");

        return false;
    }
}
