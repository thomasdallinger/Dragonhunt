package com.tharox.dragonhunt.points;

import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.shop.ShopInventory;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Used for: PlayerInteractListener, VehicleListener, EntityDeathListener, PlayerDeathListener, GameStart, ShopListener, ResetScoreboardCountdown
 */
public class Points {
    private static final HashMap<Player, Integer> points = new HashMap<>();

    public static int getPoints(Player player) {
        return points.get(player);
    }

    public static void setPoints(Player player, int pointsToSet) {
        points.put(player, pointsToSet);
    }

    public static void addPoints(Player player, int pointsToAdd, String source) {
        points.put(player, points.get(player) + pointsToAdd);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        if(player.getWorld().getName().equals("world"))
            player.sendTitle(ChatColor.AQUA + "+", ChatColor.AQUA.toString() + pointsToAdd + " Points", 5, 10, 5);
        else
            player.sendTitle(ChatColor.LIGHT_PURPLE + "+", ChatColor.LIGHT_PURPLE.toString() + pointsToAdd + " Points", 5, 10, 5);

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String pointType = source + "Points";
            String query = "";
            switch (source) {
                case "survival":
                    query = "SELECT survivalPoints FROM Plays WHERE GameID = '" + DbValues.getGameID() + "' AND Player = '" + player.getName() + "'";
                    break;
                case "mob":
                    query = "SELECT mobPoints FROM Plays WHERE GameID = '" + DbValues.getGameID() + "' AND Player = '" + player.getName() + "'";
                    break;
                case "player":
                    query = "SELECT playerPoints FROM Plays WHERE GameID = '" + DbValues.getGameID() + "' AND Player = '" + player.getName() + "'";
                    break;
                case "chest":
                    query = "SELECT chestPoints FROM Plays WHERE GameID = '" + DbValues.getGameID() + "' AND Player = '" + player.getName() + "'";
                    break;
            }

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int points = 0;

            if (rs.next()) {
                points = rs.getInt(1);
                rs.close();
                stmt.close();
            }

            points += pointsToAdd;

            switch (source) {
                case "survival":
                    query = "UPDATE Plays SET survivalPoints = '" + points + "' WHERE GameID = '" + DbValues.getGameID()
                            + "' AND Player = '" + player.getName() + "'";
                    break;
                case "mob":
                    query = "UPDATE Plays SET mobPoints = '" + points + "' WHERE GameID = '" + DbValues.getGameID()
                            + "' AND Player = '" + player.getName() + "'";
                    break;
                case "player":
                    query = "UPDATE Plays SET playerPoints = '" + points + "' WHERE GameID = '" + DbValues.getGameID()
                            + "' AND Player = '" + player.getName() + "'";
                    break;
                case "chest":
                    query = "UPDATE Plays SET chestPoints = '" + points + "' WHERE GameID = '" + DbValues.getGameID()
                            + "' AND Player = '" + player.getName() + "'";
                    break;
            }

            stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    public static void removePoints(Player player, int pointsToRemove) {
        points.put(player, points.get(player) - pointsToRemove);

        player.sendMessage(ChatColor.WHITE + "Points left: " + ChatColor.GOLD
                + Points.getPoints(player) + ChatColor.RED + " (-" + pointsToRemove + ")");
    }
}
