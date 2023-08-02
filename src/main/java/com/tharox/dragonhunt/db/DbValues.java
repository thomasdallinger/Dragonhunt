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
            String query = "SELECT player FROM plays WHERE game_id = '" + getGameID() + "'";
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
            String query = "SELECT MAX(id) FROM games";

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
            String query = "SELECT server_id FROM games WHERE id = '" + getGameID() + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            id[0] = rs.getInt(1);
            rs.close();
            stmt.close();
        });

        return id[0];
    }

    public static String getVersion() {
        final String[] version = new String[1];

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT version FROM games WHERE id = '" + getGameID() + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            version[0] = rs.getString(1);
            rs.close();
            stmt.close();
        });

        return version[0];
    }

    public static void updateServerStatus() {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE servers SET is_running = false WHERE id = '" + DbValues.getServerID() + "'";

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
                query = "SELECT COUNT(*) FROM kills WHERE game_id = '" + getGameID() + "' " +
                        "AND killer = '" + player.getUniqueId() + "'";
            } else {
                query = "SELECT COUNT(*) FROM deaths WHERE game_id = '" + getGameID() + "' " +
                        "AND player = '" + player.getUniqueId() + "'";
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

    /**
     * @return the rank of the player for the specific advancement
     */
    public static int getAdvancementRank(Player player, String advancementName) {
        final String[] playerWhoMadeAdvancement = {""};
        final int[] rank = new int[1];

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT player FROM advancements WHERE game_id = '" + getGameID() + "' "
                    + "AND advancement = '" + advancementName + "' ORDER BY id ASC";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int rowCount = 0;

            while (rs.next()) {
                rowCount++;
                playerWhoMadeAdvancement[0] = rs.getString(1); // only 1 column each row

                if (playerWhoMadeAdvancement[0].equals(player.getUniqueId().toString())) {
                    rank[0] = rowCount;
                    break;
                }
            }
            rs.close();
            stmt.close();
        });

        return rank[0];
    }

    public static String getRole(Player player) {
        final String[] role = new String[1]; // array size 1: only expecting one value

        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "SELECT role FROM players WHERE uuid = '" + player.getUniqueId() + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    role[0] = rs.getString(i);
                }
            }
            rs.close();
            stmt.close();
        });

        return role[0];
    }

    public static String getUnlockedKits(Player player) {
        final String[] kits = new String[1]; // array size 1: only expecting one value

        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "SELECT kits FROM players WHERE uuid = '" + player.getUniqueId() + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    kits[0] = rs.getString(i);
                }
            }
            rs.close();
            stmt.close();
        });

        return kits[0];
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

    public static String getTime(int time) {
        String minutes = "";
        String seconds = "";
        if ((time / 60) % 60 < 10)
            minutes = "0";
        minutes += (time / 60) % 60;
        if (time % 60 < 10)
            seconds = "0";
        seconds += time % 60;

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

    /**
     * @return the amount of credits of the specific player
     */
    public static int getCredits(Player player) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "SELECT credits FROM players WHERE uuid = '" + player.getUniqueId() + "'";

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

    /**
     * Adds the amount of credits specified to the player.
     */
    public static void addCredits(Player player, int credits) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "UPDATE players SET credits = '" + getCredits(player) + credits
                    + "' WHERE uuid = '" + player.getUniqueId() + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
