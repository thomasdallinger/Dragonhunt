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
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is currently disabled.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (player.getWorld().getName().equals("world_the_end")) {
            player.sendMessage(Prefixes.getErrorPrefix() + "This feature is disabled in the End.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (args.length == 0) {
            if (positions.containsKey(player)) {
                player.sendMessage(ChatColor.DARK_GREEN + "Your Positions:");

                for (Position position : positions.get(player)) {
                    player.sendMessage(position.toString());
                }

            } else {
                player.sendMessage(Prefixes.getErrorPrefix() + "You have not created a position yet. " +
                        "Use " + HexColor.RED + "/pos <name>" + ChatColor.RED + " to do so.");
            }

            return true;
        }

        if (args.length == 2 && args[0].equals("tp")) {
            if (positions.containsKey(player)) {
                for (Position position : positions.get(player)) {
                    if (position.getName().equals(args[1])) {
                        // Player has to have more than 5 hearts.
                        // Players lose 5 hearts upon teleportation.
                        if (player.getHealth() > 10.0) {
                            player.teleport(position.getLocation());
                            player.setHealth(player.getHealth() - 10.0);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
                            player.sendMessage(Prefixes.getTrackingPrefix() + "Teleport to "
                                    + ChatColor.DARK_GREEN + position.getName() + ChatColor.GRAY + " successful.");
                        } else {
                            player.sendMessage(Prefixes.getErrorPrefix() + "You need to have more than 5 hearts " +
                                    "to execute a position teleport.");
                            return true;
                        }
                        return true;
                    }
                }

                player.sendMessage(Prefixes.getErrorPrefix() + "Position name " + HexColor.RED + args[1]
                        + ChatColor.RED + " not found. Use /pos to see your positions.");

            } else {
                player.sendMessage(Prefixes.getErrorPrefix() + "You have not created a position yet. " +
                        "Use " + HexColor.RED + "/pos <name>" + ChatColor.RED + " to do so.");
            }
            return true;
        }

        if (args.length == 2 || args.length > 2) {
            player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/pos"
                    + ChatColor.GRAY + " to see all your positions.");
            player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/pos <name>"
                    + ChatColor.GRAY + " to add a position.");
            player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/pos tp <name>" +
                    ChatColor.GRAY + " to teleport to a position.");
            return true;
        }

        // add a position
        if (args[0].length() > 20) {
            player.sendMessage(Prefixes.getErrorPrefix()
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
                player.sendMessage(Prefixes.getErrorPrefix()
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
