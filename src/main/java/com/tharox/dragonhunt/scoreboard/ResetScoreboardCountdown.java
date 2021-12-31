package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.points.Points;
import com.tharox.dragonhunt.timer.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetScoreboardCountdown {

    public static int points = 50;
    public static int totalSeconds = 120;

    /**
     * resets the scoreboard countdown and gives points to all players once the countdown hits zero
     */
    public static void countdownHitsZero(Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (countdown.getTime() == 0) {
                    countdown.setTime(120);
                    totalSeconds += 120;

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        Points.addPoints(player, points, "survival");
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}
