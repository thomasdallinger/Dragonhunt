package com.tharox.dragonhunt.db;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

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
            String query = "SELECT player FROM plays WHERE game_id = '" + GameState.gameID + "'";
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
            String query = "SELECT server_id FROM games WHERE id = '" + GameState.gameID + "'";

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
            String query = "SELECT version FROM games WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            version[0] = rs.getString(1);
            rs.close();
            stmt.close();
        });

        return version[0];
    }

    public static String getFreeKit() {
        final String[] freeKit = new String[1];

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT free_kit FROM games WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            freeKit[0] = rs.getString(1);
            rs.close();
            stmt.close();
        });

        return freeKit[0];
    }

    public static void updateServerStatus() {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE servers SET is_running = false WHERE id = '" + GameState.gameID + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    /**
     * @return a list of all players who joined the lobby
     * (player has been added to the player table in the db)
     */
    public static ArrayList<String> getAllPlayers() {
        ArrayList<String> players = new ArrayList<>();

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT uuid FROM players";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    players.add(rs.getString(i));
                }
            }
            rs.close();
            stmt.close();
        });

        return players;
    }

    /**
     * @return the amount of wins the specific player has
     */
    public static int getWins(Player player) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT COUNT(*) FROM plays WHERE position = 1 " +
                    "AND player = '" + player.getUniqueId() + "'";

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
     * @return the rank of the specific player
     */
    public static int getRank(Player player) {
        int rank = 0;
        HashMap<String, Integer> unsortedPlayerWins = new HashMap<>();
        String[] playerUUIDs;

        // Put all players who played the game as a key in the HashMap and the corresponding number of wins as a value
        for (String uuid : getAllPlayers()) {
            unsortedPlayerWins.put(uuid, getWins(player));
        }

        // LinkedHashMap preserve the ordering of elements in which they are inserted
        LinkedHashMap<String, Integer> sortedPlayerWins = new LinkedHashMap<>(); // sorted descending

        // Use Comparator.reverseOrder() for reverse ordering
        unsortedPlayerWins.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedPlayerWins.put(x.getKey(), x.getValue()));

        playerUUIDs = sortedPlayerWins.toString().substring(1, sortedPlayerWins.toString().length() - 1)
                .trim().split(",");
        // remove { at the beginning, } at the end and all spaces
        // example: {name=5, name=4, name=3} to players[0]="name=5" players[1]="name=4" players[2]="name=3"

        for (String playerUUID : playerUUIDs) {
            rank++;
            if (playerUUID.contains(player.getUniqueId().toString()))
                break;
        }

        return rank;
    }

    /**
     * @return the elo of the specific player
     */
    public static int getElo(Player player) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "SELECT elo FROM players WHERE uuid = '" + player.getUniqueId() + "'";

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

    public static int getHighestElo(Player player) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "SELECT highest_elo FROM players WHERE uuid = '" + player.getUniqueId() + "'";

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
     * if elo > 0: add elo
     * if elo < 0: remove elo
     */
    public static void updateElo(Player player, int elo) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "UPDATE players SET elo = '" + getElo(player) + elo
                    + "' WHERE uuid = '" + player.getUniqueId() + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    /**
     * @return amount of games the specific player has played
     */
    public static int getGamesPlayed(Player player) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {

            String query = "SELECT COUNT(*) FROM plays WHERE player = '" + player.getUniqueId() + "'";

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
     * @return the time of the fastest win of the specific player
     */
    public static String getFastestWin(Player player) {
        final String[] fastestTimestamp = new String[1];

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String duration;
            int minutes, seconds;

            String query = "SELECT g.duration FROM plays p INNER JOIN games g " +
                    "ON p.game_id = g.id WHERE position = 1 AND player = '" + player.getUniqueId() + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            List<Integer> times = new ArrayList<>();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    duration = rs.getString(i);

                    minutes = Integer.parseInt(duration.substring(0, 2));
                    seconds = Integer.parseInt(duration.substring(3));
                    seconds = seconds + (minutes * 60);

                    times.add(seconds);
                }
            }

            rs.close();
            stmt.close();

            Collections.sort(times); // sorted ascending
            int fastestTime = times.get(0); // first element = lowest time
            fastestTimestamp[0] = getTime(fastestTime); // display time as string in mm:ss format
        });

        return fastestTimestamp[0];
    }

    /**
     * @param kills if true: returns amount of kills, if false: returns amount of deaths
     * @return the amount of kills/deaths the specific player has
     */
    public static int getTotalKills(Player player, boolean kills) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "";
            if (kills)
                query = "SELECT COUNT(*) FROM kills WHERE killer = '" + player.getUniqueId() + "'";
            else
                query = "SELECT COUNT(*) FROM deaths WHERE player = '" + player.getUniqueId() + "'";

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
     * @param player
     * @param kills  if true: returns amount of kills, if false: returns amount of deaths
     * @return
     */
    public static int getKills(Player player, boolean kills) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "";
            if (kills) {
                query = "SELECT COUNT(*) FROM kills WHERE game_id = '" + GameState.gameID + "' " +
                        "AND killer = '" + player.getUniqueId() + "'";
            } else {
                query = "SELECT COUNT(*) FROM deaths WHERE game_id = '" + GameState.gameID + "' " +
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
            String query = "SELECT player FROM advancements WHERE game_id = '" + GameState.gameID + "' "
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

            String query = "UPDATE players SET credits = '" + (getCredits(player) + credits)
                    + "' WHERE uuid = '" + player.getUniqueId() + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
