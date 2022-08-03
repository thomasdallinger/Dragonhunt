package com.tharox.dragonhunt.positions;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PositionCommand implements CommandExecutor {
    public HashMap<Player, String> positions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        Timer timer = Main.getInstance().getTimer();

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (!timer.isRunning()) {
            player.sendMessage(ChatColor.RED + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(ChatColor.GREEN.toString()
                    + ChatColor.ITALIC + "Use /pos [name] to add a position.");
            return true;
        }

        // print all positions
        if (args.length == 0) {
            player.sendMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "YOUR POSITIONS:");

            if (positions.containsKey(player)) { // list of all created positions
                for (String position : positions.get(player).split(";")) {
                    String[] nameAndLocation = position.split("%");
                    player.sendMessage(ChatColor.DARK_GREEN + nameAndLocation[0] + ": "
                            + ChatColor.GREEN + nameAndLocation[1]);
                }
            } else { // no positions where created
                player.sendMessage(ChatColor.GREEN.toString()
                        + ChatColor.ITALIC + "Use /pos [name] to add a position.");
            }

            return true;
        }

        // add a position
        if (args[0].length() > 20) {
            player.sendMessage(ChatColor.RED + "Position name too long (max. length: 20 characters).");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (!positions.containsKey(player)) { // first position
            positions.put(player, args[0] + "%" + DbValues.locationToString(player.getLocation()));
            // NOTE: % is just a split character
        } else { // another position
            if (positions.get(player).length() > 500) {
                player.sendMessage(ChatColor.RED + "You have exceeded the positions limit.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                return true;
            }

            positions.put(player, positions.get(player) + ";"
                    + args[0] + "%" + DbValues.locationToString(player.getLocation()));
        }

        player.sendMessage(ChatColor.GREEN + "Position "
                + ChatColor.DARK_GREEN + args[0] + ChatColor.GREEN + " added.");

        return false;
    }
}
