package com.tharox.dragonhunt.spectate;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Statement;

public class SpectateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        Timer timer = Main.getInstance().getTimer();

        if (sender instanceof Player) {
            player = (Player) sender;
        } else return true;

        if (Spectate.spectatesLeft.get(player) == 0) {
            player.sendMessage(ChatColor.RED + "You have no Spectate Tokens left.");
            return true;
        }

        if (timer.getTime() < 31) {
            player.sendMessage(ChatColor.RED + "Spectate Tokens are usable in " +
                    ChatColor.DARK_RED + (31 - timer.getTime()) + ChatColor.RED + " seconds.");
            return true;
        }

        if(!Spectate.commandUsable.get(player)) {
            player.sendMessage(ChatColor.RED + "You are currently using a Spectate Token.");
            return true;
        }

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Spectate (GameID, Player, dimension, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" +
                    DbValues.getCurrentWorld(player) + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        new Spectate(player);
        return false;
    }
}
