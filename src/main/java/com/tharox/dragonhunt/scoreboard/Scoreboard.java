package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.points.UpgradePoints;
import com.tharox.dragonhunt.timer.Countdown;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Scoreboard extends ScoreboardBuilder {

    public Scoreboard(Player player) {
        super(player, Main.getTitle());

        run(player, Main.getInstance().getScoreboardCountdown());
    }

    @Override
    public void createScoreboard() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();

        setScore(ChatColor.GRAY + dtf.format(now) + ChatColor.DARK_GRAY + " ["
                + DbValues.getServerID() + "]", 7);
        setScore(ChatColor.WHITE.toString() + ChatColor.BOLD + "1vs1"
                + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Casual", 6);
        setScore("", 5);
        setScore("", 4);
        setScore("", 3);
        setScore("", 2);
        setScore("", 1);
        setScore("", 0);
    }

    @Override
    public void update(Player player, Countdown countdown) {
        setScore(ChatColor.WHITE + "☻ " + Bukkit.getOnlinePlayers().size() + " "
                + ChatColor.WHITE + "☠ " + HexColor.GOLD + DbValues.getKills(player, true) + " "
                + ChatColor.WHITE + "✟ " + HexColor.RED + DbValues.getKills(player, false), 4);
        setScore(ChatColor.WHITE + "⛃ " + HexColor.HIGHLIGHT + UpgradePoints.getPoints(player)
                + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + countdownFormat(countdown), 2);
        setScore(HexColor.YELLOW + "playdragonhunt.net", 0);
    }

    private void run(Player player, Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {
                update(player, countdown);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private String countdownFormat(Countdown countdown) {

        String minutes = "";
        String seconds = "";
        if ((countdown.getTime() / 60) % 60 < 10)
            minutes = "0";
        minutes += (countdown.getTime() / 60) % 60;
        if (countdown.getTime() % 60 < 10)
            seconds = "0";
        seconds += countdown.getTime() % 60;

        return minutes + ":" + seconds;
    }
}
