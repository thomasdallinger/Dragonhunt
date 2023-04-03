package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.entities.EntityDropListener;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.PointsListener;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Scoreboard extends ScoreboardBuilder {
    Timer timer = Main.getInstance().getTimer();

    public Scoreboard(Player player) {
        super(player, Main.getTitle());

        run(player, Main.getInstance().getScoreboardCountdown());
    }

    @Override
    public void createScoreboard() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();

        setScore(ChatColor.GRAY + dtf.format(now) + ChatColor.DARK_GRAY + " ["
                + DbValues.getServerID() + "]", 11);
        setScore(ChatColor.WHITE + "12x1" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Casual", 10);
        setScore("", 9);
        setScore("", 8);
        setScore("", 7);
        setScore("", 6);
        setScore("", 5);
        setScore("", 4);
        setScore("", 3);
        setScore("", 2);
        setScore("", 1);
        setScore("", 0);
    }

    @Override
    public void update(Player player, Countdown countdown) {
        if (GameState.selectedKit.containsKey(player)) {
            setScore(GameState.selectedKit.get(player), 8);
        }

        setScore("☻ " + Bukkit.getOnlinePlayers().size() + " "
                + "☠ " + DbValues.getKills(player, true) + " "
                + "✟ " + DbValues.getKills(player, false), 7);

        if (Coins.XPIncrease == 0) {
            setScore(ChatColor.WHITE + "⛃ " + HexColor.HIGHLIGHT + Coins.getCoins(player)
                    + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + countdownFormat(countdown.getTime()), 5);
        } else { // XP Increase is active
            setScore(ChatColor.WHITE + "⛃ " + HexColor.HIGHLIGHT + Coins.getCoins(player)
                    + ChatColor.DARK_GRAY + " » " + HexColor.GREEN + countdownFormat(countdown.getTime())
                    + ChatColor.DARK_GREEN + " +" + Coins.XPIncrease, 5);
        }

        List<UUID> sortedKeys = sortPlayersByPoints(PointsListener.scorePerPlayer);
        boolean containsPlayer = false;
        int rankOfPlayer = 1;

        for (int index = 0; index < sortedKeys.size(); index++) {
            Player p = Bukkit.getPlayer(sortedKeys.get(index));
            if (p == null) continue;

            if (index < 3) {
                if (p == player) {
                    containsPlayer = true;
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + ChatColor.DARK_AQUA + p.getName() + " "
                            + ChatColor.AQUA + PointsListener.scorePerPlayer.get(p.getUniqueId()), 3 - index);
                } else {
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + ChatColor.WHITE + p.getName() + " "
                            + ChatColor.AQUA + PointsListener.scorePerPlayer.get(p.getUniqueId()), 3 - index);
                }
            }
            // Player is not in the top 3
            else {
                if (p == player) rankOfPlayer = index;
            }
        }

        if (!containsPlayer) {
            setScore(ChatColor.GRAY + "#" + rankOfPlayer + " "
                    + ChatColor.DARK_AQUA + player.getName() + " "
                    + ChatColor.AQUA + PointsListener.scorePerPlayer.get(player.getUniqueId()), 0);
        }
    }

    private void run(Player player, Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {
                update(player, countdown);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private String countdownFormat(int time) {

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

    private List<UUID> sortPlayersByPoints(HashMap<UUID, Integer> map) {
        // Create a list of Map.Entry objects from the HashMap
        List<Map.Entry<UUID, Integer>> list = new ArrayList<>(map.entrySet());

        // Sort the list using a custom Comparator
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // Create a new ArrayList to store the sorted keys
        List<UUID> sortedKeys = new ArrayList<>();

        // Add the sorted keys to the ArrayList
        for (Map.Entry<UUID, Integer> entry : list) {
            sortedKeys.add(entry.getKey());
        }

        // Return the sorted keys
        return sortedKeys;
    }
}
