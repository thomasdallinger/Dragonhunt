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

        if (player.getWorld().getName().equals("world_nether")) {
            player.sendMessage(ChatColor.RED + "The spectate ability is not usable in the nether.");
            return true;
        }

        if (Spectate.spectatesLeft.get(player) == 0) {
            player.sendMessage(ChatColor.RED + "You have no spectates left.");
            return true;
        }

        if (Spectate.spectateCooldown.get(player) > timer.getTime()) {
            player.sendMessage(ChatColor.RED + "Your spectate is on cooldown for: " + (Spectate.spectateCooldown.get(player) - timer.getTime()) + " seconds");
            return true;
        }

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Spectate (GameID, Player, dimension, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + DbValues.getCurrentWorld(player) + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        new Spectate(player);
        return false;
    }
}
