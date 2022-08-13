package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.upgrades.XPUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetScoreboardCountdown {
    /**
     * resets the scoreboard countdown and gives coins to all players once the countdown hits zero
     */
    public static void countdownHitsZero(Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown.getTime() == 0) {
                    countdown.setTime(300);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Coins.addCoins(player, 1);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
