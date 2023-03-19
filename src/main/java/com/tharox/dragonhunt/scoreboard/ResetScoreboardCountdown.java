package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ResetScoreboardCountdown {
    /**
     * Resets the scoreboard countdown and gives the same amount of XP
     * to all players once the countdown hits zero.
     */
    public static void countdownHitsZero(Countdown countdown) {
        new BukkitRunnable() {
            final Timer timer = Main.getInstance().getTimer();
            boolean increaseXP = false; // if true, XP will be increased for the next 3 minutes, swaps every 3 minutes

            @Override
            public void run() {
                if (countdown.getTime() == 0) {
                    countdown.setTime(180);
                    increaseXP = !increaseXP;

                    if (increaseXP) {
                        Random random = new Random();

                        if (timer.getTime() <= 600) {
                            Coins.XPIncrease = random.nextInt(4) + 1; // 1-4
                        } else if (timer.getTime() > 600 && timer.getTime() <= 1200) {
                            Coins.XPIncrease = random.nextInt(5) + 2; // 2-6
                        } else if (timer.getTime() > 1200 && timer.getTime() <= 2100) {
                            Coins.XPIncrease = random.nextInt(6) + 3; // 3-7
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + "XP increased by "
                                    + ChatColor.DARK_GREEN + ChatColor.BOLD + Coins.XPIncrease
                                    + HexColor.GREEN + " XP Points for the next "
                                    + ChatColor.DARK_GREEN + ChatColor.BOLD + "3 minutes"
                                    + HexColor.GREEN + ".");
                        }
                    } else {
                        Coins.XPIncrease = 0; // reset

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(Prefixes.getImportantPrefix() + ChatColor.RED
                                    + "XP increase has ended.");
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
