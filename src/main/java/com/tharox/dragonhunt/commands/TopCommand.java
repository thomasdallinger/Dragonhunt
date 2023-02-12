package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int cost = 5;
        Player player;
        World world;
        double x, z;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (PaidCommandsValidator.commandExecutionIsValid(player, cost)) return true;

        world = player.getWorld();
        x = player.getLocation().getX();
        z = player.getLocation().getZ();

        if (player.getLocation().getY() > world.getHighestBlockYAt((int) x, (int) z)) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "Already on the surface.");
            return true;
        }

        Coins.removeCoins(player, cost, "/top");
        player.teleport(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z));
        player.sendMessage(Prefixes.getAllPrefix() + HexColor.GREEN + "Teleported to the surface.");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        return false;
    }
}
