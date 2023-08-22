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
     * Resets the scoreboard countdown which has different effects (XP increase, new scout available)
     */
    public static void countdownHitsZero(Countdown countdown) {
        new BukkitRunnable() {
            final Timer timer = Main.getInstance().getTimer();
            boolean increaseXP = false; // if true, XP will be increased for the next 3 minutes, swaps every 3 minutes

            @Override
            public void run() {
                if (countdown.getTime() == 0) {
                    countdown.setTime(180);

                    // Make a new scout available
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Scout.scoutAvailable.put(player, true);
                        player.sendMessage(Prefixes.getImportantPrefix() + ChatColor.WHITE + "/scout"
                                + ChatColor.GRAY + " is now " + HexColor.GREEN + "available"
                                + ChatColor.GRAY + " for all players.");
                    }

                    // XP increase
                    increaseXP = !increaseXP;

                    if (increaseXP) {
                        Random random = new Random();

                        if (timer.getTime() <= 600) {
                            Coins.XPIncrease = random.nextInt(4) + 1; // 1-4
                        } else if (timer.getTime() > 600 && timer.getTime() <= 1200) {
                            Coins.XPIncrease = random.nextInt(4) + 2; // 2-5
                        } else if (timer.getTime() > 1200 && timer.getTime() <= 1800) {
                            Coins.XPIncrease = random.nextInt(4) + 3; // 3-6
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + "XP increased by "
                                    + ChatColor.DARK_GREEN + Coins.XPIncrease
                                    + HexColor.GREEN + " XP Points for the next " + ChatColor.DARK_GREEN + "3"
                                    + HexColor.GREEN + " minutes.");
                        }
                    } else {
                        // Reset the XP increase.
                        Coins.XPIncrease = 0;

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED
                                    + "XP increase has ended.");
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
