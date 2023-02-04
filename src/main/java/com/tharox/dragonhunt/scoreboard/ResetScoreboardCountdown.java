package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetScoreboardCountdown {
    /**
     * resets the scoreboard countdown and gives the same amount of coins to all players once the countdown hits zero
     */
    public static void countdownHitsZero(Countdown countdown) {
        new BukkitRunnable() {
            final Timer timer = Main.getInstance().getTimer();

            @Override
            public void run() {
                if (countdown.getTime() == 0) {
                    countdown.setTime(300);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Coins.addXP(player, Coins.bonusXP);
                        player.sendMessage(Prefixes.getXPPrefix(Coins.bonusXP) + ChatColor.GRAY + "Survived "
                                + ChatColor.YELLOW + (timer.getTime() / 60) + ChatColor.GRAY + " minutes.");
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
