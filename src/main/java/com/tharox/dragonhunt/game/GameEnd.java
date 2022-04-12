package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Statement;

public class GameEnd {

    Player winner;
    Timer timer = Main.getInstance().getTimer();
    Timer scoreboardCountdown = Main.getInstance().getScoreboardCountdown();

    /**
     * no winner - time ran out
     */
    public GameEnd() {
        configureGameEnd();
    }

    public GameEnd(Player winner) {
        this.winner = winner;

        timer.setRunning(false);
        scoreboardCountdown.setRunning(false);

        updateGame();
        configureGameEnd();
        serverStopsCountdown();
    }

    private void updateGame() {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE Game SET duration = '" + DbValues.getTimestamp()
                    + "', dungeonLocation = '" + DbValues.locationToString(GameState.dungeonLocation)
                    + "' WHERE id = '" + DbValues.getGameID() + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    private void configureGameEnd() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Main.getPrefix() + ChatColor.RED + ChatColor.BOLD + "GAME OVER.");

            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);

            if (player == winner) {
                player.sendTitle(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "YOU WON", ChatColor.GREEN
                        + "You defeated the Enderdragon", 10, 90, 20);
                addWinnerAndXPLevel(player, true);
            } else {
                player.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "YOU LOST", ChatColor.RED
                        + "Try again next time", 10, 90, 20);
                addWinnerAndXPLevel(player, false);
            }

            if (winner != null) {
                player.sendMessage(Main.getPrefix() + ChatColor.GOLD + ChatColor.BOLD + winner.getName().toUpperCase() + " has won the Dragonhunt!");
            }

            player.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "TIME PLAYED: " + ChatColor.WHITE
                    + DbValues.getTimestamp());
            //TODO: Game Stats messages?
        }
    }

    private void addWinnerAndXPLevel(Player player, boolean hasWon) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query;

            if (hasWon) {
                query = "UPDATE Plays SET hasWon = true, XPLevel = '" + player.getLevel() + "'" +
                        "WHERE GameID = '" + DbValues.getGameID() + "' AND Player = '" + player.getName() + "'";
            } else {
                query = "UPDATE Plays SET hasWon = false, XPLevel = '" + player.getLevel() + "'" +
                        "WHERE GameID = '" + DbValues.getGameID() + "' AND Player = '" + player.getName() + "'";
            }

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    private void serverStopsCountdown() {
        final int[] duration = {90};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (duration[0] <= 0) {
                    GameState.movePlayersToLobby();
                }

                if (duration[0] <= 10) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    }
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED.toString() + ChatColor.BOLD
                            + "BACK TO LOBBY IN " + duration[0] + " SECONDS"));
                }
                duration[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}
