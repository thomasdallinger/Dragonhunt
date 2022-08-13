package com.tharox.dragonhunt.commands;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.spectate.Spectate;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Statement;

public class SpectateCommand extends CommandHelper implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        Timer timer = Main.getInstance().getTimer();

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (checkIfValid(player, timer)) return true;

        if (!Spectate.commandUsable.get(player)) {
            player.sendMessage(ChatColor.RED + "You are currently in a Spectate.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        if (Coins.getCoins(player) < getCommandCost(Main.getInstance().getConfig(), "/spec")) {
            player.sendMessage(ChatColor.RED + "Not enough coins.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return true;
        }

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Spectate (GameID, Player, dimension, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" +
                    DbValues.getCurrentWorld(player.getLocation()) + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        Coins.removeCoins(player, getCommandCost(Main.getInstance().getConfig(), "/spec"));
        new Spectate(player);
        return false;
    }
}
