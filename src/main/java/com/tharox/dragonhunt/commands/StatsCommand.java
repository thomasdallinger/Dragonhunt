package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return false;

        if (args.length == 0) {
            player.sendMessage(HexColor.YELLOW.toString() + ChatColor.BOLD + player.getName() + "'s"
                    + ChatColor.WHITE + ChatColor.BOLD + " Stats:");

            player.sendMessage("Rank: " + ChatColor.GOLD + DbValues.getRank(player)
                    + ChatColor.GRAY + " (" + DbValues.getElo(player) + " Elo)");

            player.sendMessage("Highest Elo: " + HexColor.YELLOW + DbValues.getHighestElo(player));

            if (DbValues.getTotalKills(player, false) > 0) { // would be a division by 0
                player.sendMessage("Kills: " + HexColor.YELLOW + DbValues.getTotalKills(player, true)
                        + ChatColor.GRAY + " (K/D: " + roundDecimalNumber(((double) DbValues.getTotalKills(player, true)
                        / DbValues.getTotalKills(player, false)), 2) + ")");
            } else {
                player.sendMessage("Kills: " + HexColor.YELLOW + DbValues.getTotalKills(player, true)
                        + ChatColor.GRAY + " (K/D: " + DbValues.getTotalKills(player, true) + ")");
            }

            player.sendMessage("Deaths: " + HexColor.YELLOW + DbValues.getTotalKills(player, false));
            player.sendMessage("Games: " + HexColor.YELLOW + DbValues.getGamesPlayed(player));
            player.sendMessage("Wins: " + HexColor.YELLOW + DbValues.getWins(player));

            if (DbValues.getGamesPlayed(player) != 0) { // would be a division by 0
                player.sendMessage("Win Rate: " + HexColor.YELLOW
                        + roundDecimalNumber(((double) DbValues.getWins(player)
                        / DbValues.getGamesPlayed(player)) * 100, 2) + "%");
            } else {
                player.sendMessage("Win Rate: " + HexColor.YELLOW + "0%");
            }

            if (DbValues.getWins(player) > 0) { // check if the player has won a game
                player.sendMessage("Fastest: "
                        + HexColor.YELLOW + DbValues.getFastestWin(player));
            } else {
                player.sendMessage("Fastest: " + HexColor.YELLOW + "--:--");
            }
        } else if (args.length == 1) {
            Player p = Bukkit.getPlayer(args[0]);

            if (p == null) {
                player.sendMessage(Prefixes.getErrorPrefix() + "Player " + ChatColor.DARK_RED + args[0]
                        + ChatColor.RED + " not found. " +
                        "You can only search for players that are currently in your match.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                return false;
            }

            player.sendMessage(HexColor.YELLOW.toString() + ChatColor.BOLD + p.getName() + "'s"
                    + ChatColor.WHITE + ChatColor.BOLD + " Stats:");

            player.sendMessage("Rank: " + ChatColor.GOLD + DbValues.getRank(p)
                    + ChatColor.GRAY + " (" + DbValues.getElo(p) + " Elo)");

            player.sendMessage("Highest Elo: " + HexColor.YELLOW + DbValues.getHighestElo(p));

            if (DbValues.getTotalKills(p, false) > 0) { // would be a division by 0
                player.sendMessage("Kills: " + HexColor.YELLOW + DbValues.getTotalKills(p, true)
                        + ChatColor.GRAY + " (K/D: " + roundDecimalNumber(((double) DbValues.getTotalKills(p, true)
                        / DbValues.getTotalKills(p, false)), 2) + ")");
            } else {
                player.sendMessage("Kills: " + HexColor.YELLOW + DbValues.getTotalKills(p, true)
                        + ChatColor.GRAY + " (K/D: " + DbValues.getTotalKills(p, true) + ")");
            }

            player.sendMessage("Deaths: " + HexColor.YELLOW + DbValues.getTotalKills(p, false));
            player.sendMessage("Games: " + HexColor.YELLOW + DbValues.getGamesPlayed(p));
            player.sendMessage("Wins: " + HexColor.YELLOW + DbValues.getWins(p));

            if (DbValues.getGamesPlayed(p) != 0) { // would be a division by 0
                player.sendMessage("Win Rate: " + HexColor.YELLOW
                        + roundDecimalNumber(((double) DbValues.getWins(p)
                        / DbValues.getGamesPlayed(p)) * 100, 2) + "%");
            } else {
                player.sendMessage("Win Rate: " + HexColor.YELLOW + "0%");
            }

            if (DbValues.getWins(p) > 0) { // check if the player has won a game
                player.sendMessage("Fastest: "
                        + HexColor.YELLOW + DbValues.getFastestWin(p));
            } else {
                player.sendMessage("Fastest: " + HexColor.YELLOW + "--:--");
            }
        } else {
            player.sendMessage(Prefixes.getErrorPrefix() + "This command cannot have more than one argument. " +
                    "Use /stats or /stats <playername>");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
        }

        return true;
    }

    /**
     * Rounds the passed value to the number of passed decimal places.
     *
     * @param value         is the value to be rounded.
     * @param decimalPoints is the number of decimal places to round to.
     */
    private double roundDecimalNumber(double value, int decimalPoints) {
        double d = Math.pow(10, decimalPoints);
        return Math.round(value * d) / d;
    }
}
