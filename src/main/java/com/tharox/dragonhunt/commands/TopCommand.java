package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand extends CommandErrors implements CommandExecutor {
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

        player.teleport(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z));
        player.sendMessage(HexColor.GREEN + "Whoosh.");
        Coins.removeCoins(player, 1);

        return false;
    }
}
