package com.tharox.dragonhunt.db;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbValues {

    // a list containing all players that have queued up to play the game
    public static List<String> playersAllowedToPlay = new ArrayList<>();

    /**
     * Add all players who are allowed to join the server to a list.
     * Clear the list each time the method is executed.
     */
    public static void getPlayersAllowedToPlay() {
        playersAllowedToPlay.clear();

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT Player FROM Plays WHERE GameID = '" + getGameID() + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    playersAllowedToPlay.add(rs.getString(i));
                }
            }
        });
    }

    /**
     *
     */
    public static int getGameID() {
        final int[] id = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT MAX(id) FROM Game";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    id[0] = rs.getInt(i);
                }
            }
            rs.close();
            stmt.close();
        });

        return id[0];
    }

    public static int getServerID() {
        final int[] id = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT ServerID FROM Game WHERE id = '" + getGameID() + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            id[0] = rs.getInt(1);
            rs.close();
            stmt.close();
        });

        return id[0];
    }

    public static void updateServerStatus() {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE Servers SET isRunning = false WHERE id = '" + DbValues.getServerID() + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    /**
     * @param player
     * @param kills  if true: returns amount of kills, if false: returns amount of deaths
     * @return
     */
    public static int getKills(Player player, boolean kills) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "";
            if (kills) {
                query = "SELECT COUNT(*) FROM Kills WHERE GameID = '" + getGameID() + "' " +
                        "AND Killer = '" + player.getName() + "'";
            } else {
                query = "SELECT COUNT(*) FROM Deaths WHERE GameID = '" + getGameID() + "' " +
                        "AND Player = '" + player.getName() + "'";
            }

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    amount[0] = rs.getInt(i);
                }
            }
            rs.close();
            stmt.close();
        });

        return amount[0];
    }

    public static String getTimestamp() {
        Timer timer = Main.getInstance().getTimer();
        String minutes = "";
        String seconds = "";
        if ((timer.getTime() / 60) % 60 < 10)
            minutes = "0";
        minutes += (timer.getTime() / 60) % 60;
        if (timer.getTime() % 60 < 10)
            seconds = "0";
        seconds += timer.getTime() % 60;

        return minutes + ":" + seconds;
    }

    public static String getCurrentWorld(Location location) {
        String currentWorld = "Unknown";

        if (location.getWorld() != null) {
            switch (location.getWorld().getName()) {
                case "world":
                    currentWorld = "Overworld";
                    break;
                case "world_nether":
                    currentWorld = "Nether";
                    break;
                case "world_the_end":
                    currentWorld = "The End";
                    break;
            }
        }

        return currentWorld;
    }
}
