package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Statement;

public class GameEnd {
    static Player winner;
    Timer timer = Main.getInstance().getTimer();
    Timer scoreboardCountdown = Main.getInstance().getScoreboardCountdown();

    public GameEnd() {
        // TIMER and SCOREBOARD COUNTDOWN are no longer running after the game ends
        timer.setRunning(false);
        scoreboardCountdown.setRunning(false);

        updateGame();
        configureGameEnd();
        serverStopsCountdown();
    }

    /**
     * Only used when there is a winner, meaning the time didn't run out
     */
    public GameEnd(Player winner) {
        GameEnd.winner = winner;
        new GameEnd();
    }

    /**
     * Adds certain values to the database, which usually cannot be added before the game ended.
     */
    private void updateGame() {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE Game SET duration = '" + DbValues.getTimestamp()
                    + "', distanceScore = '" + GameState.distanceScore + "' WHERE id = '" + DbValues.getGameID() + "'";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }

    /**
     * All players will see a message and a title on their screen which vary depending on if they won or lost.
     * Players will be in SPECTATOR GAMEMODE.
     * The total time played will be displayed, too.
     */
    private void configureGameEnd() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Prefixes.getPrefix() + ChatColor.GOLD + ChatColor.BOLD + "GAME OVER.");

            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);

            // no winner != null check because the else block still executes if the winner is null
            if (player == winner) {
                player.sendTitle(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "YOU WON",
                        HexColor.GREEN + "You defeated the Enderdragon.", 10, 90, 20);
                addWinnerAndXPLevel(player, true);
            } else {
                player.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "YOU LOST",
                        HexColor.RED + "Try again next time.", 10, 90, 20);
                addWinnerAndXPLevel(player, false);
            }

            if (winner != null) {
                player.sendMessage(Prefixes.getPrefix() + ChatColor.YELLOW + winner.getName()
                        + ChatColor.GRAY + " has won the Dragonhunt!");
            }

            player.sendMessage(Prefixes.getPrefix() + ChatColor.GRAY + "Time played: "
                    + ChatColor.YELLOW + DbValues.getTimestamp());
            //TODO: Game Stats messages?
        }
    }

    /**
     * Adds certain values to the database, which cannot be added before the game ended.
     *
     * @param player player who played the game
     * @param hasWon whether this player has won or not
     */
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

    /**
     * The server stops 90 seconds after the game has ended.
     * All players will be moved to the lobby after 90 seconds, if they don't leave before.
     */
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
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                            HexColor.RED.toString() + ChatColor.BOLD + "BACK TO LOBBY IN "
                                    + ChatColor.DARK_RED + ChatColor.BOLD + duration[0]
                                    + HexColor.RED + ChatColor.BOLD + " SECONDS"));
                }
                duration[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
