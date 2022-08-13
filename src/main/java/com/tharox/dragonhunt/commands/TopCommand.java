package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
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

public class TopCommand extends CommandHelper implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        World world;
        double x, z;
        Timer timer = Main.getInstance().getTimer();

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        world = player.getWorld();
        x = player.getLocation().getX();
        z = player.getLocation().getZ();

        if (checkIfValid(player, timer)) return true;

        if (player.getLocation().getY() > world.getHighestBlockYAt((int) x, (int) z)) {
            player.sendMessage(HexColor.RED + "Already on the surface.");
            return true;
        }

        if (Coins.getCoins(player) < getCommandCost(Main.getInstance().getConfig(), "/top")) {
            player.sendMessage(ChatColor.RED + "You don't have enough coins! Need "
                    + (getCommandCost(Main.getInstance().getConfig(), "/top") - Coins.getCoins(player))
                    + " more.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Coins.removeCoins(player, getCommandCost(Main.getInstance().getConfig(), "/top"));
        player.teleport(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z));
        player.sendMessage(HexColor.GREEN + "Whoosh.");

        return false;
    }
}
