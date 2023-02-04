package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.positions.Position;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PositionCommand implements CommandExecutor {
    public static HashMap<Player, List<Position>> positions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        Timer timer = Main.getInstance().getTimer();

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (!timer.isRunning()) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (player.getWorld().getName().equals("world_the_end")) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "This feature is disabled in the End.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "Use "
                    + HexColor.RED + "/pos [name]" + ChatColor.GRAY + " to add a position.");
            return true;
        }

        // add a position
        if (args[0].length() > 20) {
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY
                    + "Failed to create a position. Name too long (max. length: 20 characters).");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (!positions.containsKey(player)) { // first position
            positions.put(player, Collections.singletonList(new Position(args[0], player.getLocation())));
        } else { // another position
            List<Position> updatedList = new ArrayList<>(positions.get(player)); // add all existing positions
            updatedList.add(new Position(args[0], player.getLocation())); // add new position

            if (updatedList.size() > 20) {
                player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY
                        + "Failed to create a position. You have exceeded the limit of 20 positions.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                return true;
            }

            positions.put(player, updatedList);
        }

        player.sendMessage(Prefixes.getTrackingPrefix() + ChatColor.GRAY + "Position "
                + ChatColor.DARK_GREEN + args[0] + ChatColor.GRAY + " added.");

        return false;
    }
}
