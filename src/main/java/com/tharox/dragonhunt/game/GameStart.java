package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.arrows.Arrows;
import com.tharox.dragonhunt.brewing.Effects;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.elytra.ElytraSwapRunnable;
import com.tharox.dragonhunt.enchanting.Enchantments;
import com.tharox.dragonhunt.modifiers.classes.EndlessNight;
import com.tharox.dragonhunt.modifiers.selection.ModifierInventory;
import com.tharox.dragonhunt.players.PlayerDamageListener;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class GameStart extends GameConfiguration {
    Timer timer = Main.getInstance().getTimer();
    Countdown startCountdown, gracePeriodCountdown;
    Countdown scoreboardCountdown = Main.getInstance().getScoreboardCountdown();
    ModifierInventory modifierInventory;
    BossBar bossBar;
    World overworld = Bukkit.getServer().getWorld("world");
    Location startLocation;
    double x = overworld != null ? overworld.getSpawnLocation().getX() : 0, highestY,
            z = overworld != null ? overworld.getSpawnLocation().getZ() : 0;
    // used if the highest block at x: 0 and z: 0 is within 55 blocks of the startLocation

    public GameStart() {
        startCountdown = new Countdown(true, 25, this::onCountdown);
        modifierInventory = new ModifierInventory();
        highestY = overworld.getHighestBlockYAt((int) x, (int) z);
        startLocation = new Location(overworld, x, highestY + 55, z);

        configureOverworld(x, z);
        configureNether();
        configureEnd();
        configurePlayer();

        new Enchantments(); // start the runnable
        new Effects(); // start the runnable
    }

    /**
     * Countdown which is executed each second (see Countdown.java).
     *
     * @param time time of the countdown
     */
    public void onCountdown(int time) {
        if (overworld == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix() + HexColor.RED + "World name is incorrect.");
            return;
        }

        // once the countdown hits zero, start the TIMER and SCOREBOARD COUNTDOWN
        if (startCountdown.getTime() == 0) {
            timer.setRunning(true);
            scoreboardCountdown.setRunning(true);

            gracePeriodCountdown = new Countdown(true, 60, this::onGracePeriodCountdown);
            bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_6);
            PlayerDamageListener.gracePeriodActive = true;

            new Arrows(); // start arrow generation
            new ElytraSwapRunnable().runTaskTimer(Main.getInstance(), 0, 5);

            if (!EndlessNight.active)
                overworld.setTime(0);
            else
                new PermanentNight();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (startCountdown.getTime() == 25) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.openInventory(modifierInventory.modifiers);
            }

            if (startCountdown.getTime() == 10) {
                GameState.modifierInventoryClose = true;
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() <= 10 && startCountdown.getTime() > 3) {
                player.sendTitle(HexColor.HIGHLIGHT + "Starting in",
                        ChatColor.WHITE.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);
            }

            if (startCountdown.getTime() == 3) {
                player.sendTitle(HexColor.HIGHLIGHT + "Starting in",
                        HexColor.RED.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() == 2) {
                player.sendTitle(HexColor.HIGHLIGHT + "Starting in",
                        ChatColor.GOLD.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() == 1) {
                player.sendTitle(HexColor.HIGHLIGHT + "Starting in",
                        HexColor.GREEN.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.teleport(startLocation);
                player.setGameMode(GameMode.SURVIVAL);

                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GOLD + ChatColor.BOLD
                        + "Whoever kills the Enderdragon wins.");
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.YELLOW + "/pos [name]"
                        + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + "mark locations");
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.YELLOW + "/top" + ChatColor.DARK_GRAY + ": "
                        + ChatColor.GRAY + "get to the surface quickly");
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.YELLOW + "/respawn" + ChatColor.DARK_GRAY + ": "
                        + ChatColor.GRAY + "sets a respawn point");
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.YELLOW + "/spec" + ChatColor.DARK_GRAY + ": "
                        + ChatColor.GRAY + "go into spectator mode");
            }
        }
    }

    /**
     * Executed after the timer starts.
     * 60s BossBar
     *
     * @param time time of the countdown
     */
    public void onGracePeriodCountdown(int time) {
        bossBar.setTitle(HexColor.GREEN + "Grace Period ends in " + ChatColor.DARK_GREEN
                + ChatColor.BOLD + gracePeriodCountdown.getTime() + HexColor.GREEN + " seconds");
        bossBar.setProgress(time / 61D);
        bossBar.setVisible(true);

        if (gracePeriodCountdown.getTime() == 0) {
            PlayerDamageListener.gracePeriodActive = false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (gracePeriodCountdown.getTime() == 60) {
                bossBar.addPlayer(player);
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "The Grace Period ends in "
                        + HexColor.GREEN + "60" + ChatColor.GRAY + " seconds.");
            }

            if (gracePeriodCountdown.getTime() == 30 || gracePeriodCountdown.getTime() == 15
                    || (gracePeriodCountdown.getTime() <= 5 && gracePeriodCountdown.getTime() > 0)) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "The Grace Period ends in "
                        + HexColor.GREEN + gracePeriodCountdown.getTime() + ChatColor.GRAY + " seconds.");
            }

            if (gracePeriodCountdown.getTime() == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.sendMessage(Prefixes.getAllPrefix() + HexColor.GREEN + "The Grace Period has ended.");
                bossBar.removePlayer(player);
                bossBar.setVisible(false);
            }
        }
    }
}
