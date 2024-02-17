package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.scout.Scout;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ResetScoreboardCountdown {
    /**
     * Resets the scoreboard countdown which has different effects (new scout available)
     */
    public static void countdownHitsZero(Countdown countdown) {
        new BukkitRunnable() {
            final Timer timer = Main.getInstance().getTimer();

            @Override
            public void run() {
                if (countdown.getTime() == 0) {
                    countdown.setTime(180);

                    // Make a new scout available
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        // Currently unused
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
