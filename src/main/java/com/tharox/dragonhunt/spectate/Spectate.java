package com.tharox.dragonhunt.spectate;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class Spectate {
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    public static final int[] duration = {12};

    public Spectate(Player player) {
        activateSpectate(player);
        addSpectateToDB(player);
    }

    private void activateSpectate(Player player) {
        spectateLocation.put(player, player.getLocation());

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.setGameMode(GameMode.SPECTATOR);

        spectateDurationLeft(player);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Prefixes.getAllPrefix() + ChatColor.YELLOW + player.getName()
                    + ChatColor.GRAY + " uses a Spectate.");
        }
    }

    private void spectateDurationLeft(Player player) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (duration[0] <= 0) {
                    player.performCommand("pos Spectate" + getSpectateAmount(player));
                    player.teleport(Spectate.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    cancel();
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString()
                        + ChatColor.BOLD + duration[0] + ChatColor.YELLOW + ChatColor.BOLD + " SECONDS LEFT"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                duration[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    private void addSpectateToDB(Player player) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO Spectate (GameID, Player, dimension, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" +
                    DbValues.getCurrentWorld(player.getLocation()) + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    /**
     * @return the amount of spectates used by a player in this game
     */
    public static int getSpectateAmount(Player player) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT COUNT(*) FROM Spectate WHERE GameID = '" + DbValues.getGameID() + "' " +
                    "AND Player = '" + player.getName() + "'";

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
}
