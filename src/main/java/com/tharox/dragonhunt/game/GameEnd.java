package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.players.CombatListener;
import com.tharox.dragonhunt.players.PointsListener;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class GameEnd {
    static Player winner;
    Timer timer = Main.getInstance().getTimer();
    Timer scoreboardCountdown = Main.getInstance().getScoreboardCountdown();

    public GameEnd() {
        // TIMER and SCOREBOARD COUNTDOWN are no longer running after the game ends
        timer.setRunning(false);
        scoreboardCountdown.setRunning(false);
        winner = Bukkit.getPlayer(GameState.sortPlayersByPoints(PointsListener.scorePerPlayer).get(0));

        updateGame();
        configureGameEnd();
        serverStopsCountdown();
    }

    /**
     * Adds certain values to the database, which usually cannot be added before the game ended.
     */
    private void updateGame() {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE games SET duration = '" + DbValues.getTimestamp()
                    + "' WHERE id = '" + GameState.gameID + "'";

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
            player.sendMessage(Prefixes.getImportantPrefix() + HexColor.YELLOW + ChatColor.BOLD + "GAME OVER.");

            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);

            CombatListener.combatList.remove(player.getUniqueId());

            player.sendMessage(ChatColor.WHITE.toString() + ChatColor.BOLD + winner.getName()
                    + HexColor.GREEN + " has won the Dragonhunt.");

            player.sendMessage(ChatColor.GRAY + "Time played: "
                    + HexColor.YELLOW + DbValues.getTimestamp());

            player.sendMessage(ChatColor.DARK_GRAY + "\nꜰɪɴᴀʟ ꜱᴛᴀɴᴅɪɴɢꜱ");

            List<UUID> sortedKeys = GameState.sortPlayersByPoints(PointsListener.scorePerPlayer);

            for (int index = 0; index < sortedKeys.size(); index++) {
                Player p = Bukkit.getPlayer(sortedKeys.get(index));
                if (p == null) continue;

                if (p == player) {
                    player.sendMessage(ChatColor.GRAY + "#" + (index + 1) + " "
                            + HexColor.YELLOW + p.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(p.getUniqueId()));

                    player.sendTitle(HexColor.YELLOW + "#" + (index + 1),
                            ChatColor.WHITE + "You scored "
                                    + HexColor.YELLOW + PointsListener.scorePerPlayer.get(p.getUniqueId())
                                    + ChatColor.WHITE + " Points",
                            10, 90, 20);

                    addDataToPlaysTable(player, (index + 1));
                } else {
                    player.sendMessage(ChatColor.GRAY + "#" + (index + 1) + " "
                            + ChatColor.WHITE + p.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(p.getUniqueId()));
                }
            }

            // Give the player credits only after the game ended. The amount is determined by the points scored.
            DbValues.addCredits(player, PointsListener.scorePerPlayer.get(player.getUniqueId()));
            player.sendMessage(ChatColor.GRAY + "\nYou have earned " + HexColor.YELLOW + ChatColor.BOLD
                    + PointsListener.scorePerPlayer.get(player.getUniqueId()) + " Credits" + ChatColor.GRAY + ".");
            player.sendMessage(ChatColor.GRAY + "You now have " + HexColor.YELLOW
                    + DbValues.getCredits(player) + ChatColor.GRAY + " Credits" + ".");
        }
    }

    /**
     * Adds certain values to the database, which cannot be added before the game ended.
     */
    private void addDataToPlaysTable(Player player, int position) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "UPDATE plays SET position = '" + position + "', "
                    + "points = '" + PointsListener.scorePerPlayer.get(player.getUniqueId()) + "', "
                    + "xp_level = '" + player.getLevel() + "'"
                    + "WHERE game_id = '" + GameState.gameID + "' AND player = '" + player.getUniqueId() + "'";
            ;

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();

            if (GameState.selectedKit.containsKey(player)) {
                // Substring 14 from kit name to remove HEX formatting
                query = "UPDATE plays SET kit = '" + GameState.selectedKit.get(player).substring(14) + "'" +
                        "WHERE game_id = '" + GameState.gameID + "' AND player = '" + player.getUniqueId() + "'";
            }

            stmt = con.createStatement();
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
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder("BACK TO LOBBY").color(HexColor.YELLOW).bold(true)
                                    .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                    .append(duration[0] + " seconds").color(ChatColor.GRAY)
                                    .create());
                }
                duration[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
