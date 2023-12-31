package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.PlayerEndTeleport;
import com.tharox.dragonhunt.players.PointsListener;
import com.tharox.dragonhunt.scout.Scout;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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

        setScore(ChatColor.GRAY + dtf.format(now) + ChatColor.DARK_GRAY + " v" + DbValues.getVersion() + " ["
                + DbValues.getServerID() + "]", 13);
        setScore("", 12);
        setScore("", 11);
        setScore("", 10);
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
        if (GameState.eloPerPlayer.getOrDefault(player, 0) >= 0) {
            setScore(HexColor.GREEN + "+" + GameState.eloPerPlayer.getOrDefault(player, 0)
                    + ChatColor.WHITE + " ✪", 12);
        } else {
            setScore(HexColor.RED.toString() + GameState.eloPerPlayer.getOrDefault(player, 0)
                    + ChatColor.WHITE + " ✪", 12);
        }

        if (GameState.selectedKit.containsKey(player)) {
            setScore(GameState.selectedKit.get(player), 10);
        }

        setScore("☻ " + Bukkit.getOnlinePlayers().size() + " "
                + "☠ " + DbValues.getKills(player, true) + " "
                + "✟ " + DbValues.getKills(player, false), 9);

        if (Scout.scoutAvailable.containsKey(player)) {
            if (Scout.scoutAvailable.get(player)) {
                if (timer.getTime() % 2 == 0) {
                    setScore("/scout available", 7);
                } else {
                    setScore(ChatColor.GOLD + "/scout" + ChatColor.WHITE + " available", 7);
                }
            } else {
                setScore("", 7);
            }
        }

        if (timer.getTime() <= 900 && !GameState.playersReachedEnd) {
            setScore(ChatColor.BOLD.toString() + PlayerEndTeleport.getEyesNeeded()
                    + ChatColor.WHITE + " Eyes" + ChatColor.DARK_GRAY + " » "
                    + ChatColor.GRAY + countdownFormat(900 - timer.getTime()), 6);
        } else {
            setScore(HexColor.GREEN.toString() + ChatColor.BOLD + PlayerEndTeleport.getEyesNeeded()
                    + ChatColor.WHITE + " Eyes", 6);
        }

        setScore(Coins.getCoins(player) + " ⛃" + ChatColor.DARK_GRAY + " » "
                + ChatColor.GRAY + "/shop", 5);

        List<UUID> sortedKeys = GameState.sortPlayersByPoints(PointsListener.scorePerPlayer);

        for (int index = 0; index < sortedKeys.size(); index++) {
            Player p = Bukkit.getPlayer(sortedKeys.get(index));
            if (p == null) continue;

            if (index < 3) {
                if (p == player) {
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + HexColor.YELLOW + p.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(p.getUniqueId()), 3 - index);
                } else {
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + ChatColor.WHITE + p.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(p.getUniqueId()), 3 - index);
                }
            }
            // Player is not in the top 3
            else {
                if (p == player) {
                    setScore(ChatColor.GRAY + "#" + (index + 1) + " "
                            + HexColor.YELLOW + player.getName() + " "
                            + HexColor.YELLOW + PointsListener.scorePerPlayer.get(player.getUniqueId()), 0);
                }
            }
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
}
