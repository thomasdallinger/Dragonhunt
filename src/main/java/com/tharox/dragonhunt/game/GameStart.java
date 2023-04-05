package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.arrows.Arrows;
import com.tharox.dragonhunt.brewing.Effects;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.enchanting.Enchantments;
import com.tharox.dragonhunt.kits.selection.KitInventory;
import com.tharox.dragonhunt.modifiers.classes.BonusHearts;
import com.tharox.dragonhunt.modifiers.classes.EndlessNight;
import com.tharox.dragonhunt.modifiers.selection.ModifierInventory;
import com.tharox.dragonhunt.movement.ElytraSwapRunnable;
import com.tharox.dragonhunt.players.PlayerDamageListener;
import com.tharox.dragonhunt.players.PointsListener;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameStart extends GameConfiguration {
    Timer timer = Main.getInstance().getTimer();
    Countdown startCountdown, gracePeriodCountdown;
    Countdown scoreboardCountdown = Main.getInstance().getScoreboardCountdown();
    ModifierInventory modifierInventory;
    KitInventory kitInventory = new KitInventory();
    BossBar bossBar;
    World overworld = Bukkit.getServer().getWorld("world");
    Location startLocation;
    double x = overworld != null ? overworld.getSpawnLocation().getX() : 0, highestY,
            z = overworld != null ? overworld.getSpawnLocation().getZ() : 0;
    // used if the highest block at x: 0 and z: 0 is within 55 blocks of the startLocation

    public GameStart() {
        startCountdown = new Countdown(true, 30, this::onCountdown);
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
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix() + "World name is incorrect.");
            return;
        }

        // once the countdown hits zero, start the TIMER and SCOREBOARD COUNTDOWN
        if (startCountdown.getTime() == 0) {
            timer.setRunning(true);
            scoreboardCountdown.setRunning(true);

            gracePeriodCountdown = new Countdown(true, GameState.gracePeriodTime, this::onGracePeriodCountdown);
            bossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SEGMENTED_10);
            PlayerDamageListener.gracePeriodActive = true;

            new Arrows(); // start arrow generation
            new ElytraSwapRunnable().runTaskTimer(Main.getInstance(), 0, 5);

            if (!EndlessNight.active)
                overworld.setTime(0);
            else
                new PermanentNight();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (startCountdown.getTime() == 30) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.openInventory(modifierInventory.modifiers);
            }

            if (startCountdown.getTime() == 20) {
                GameState.modifierInventoryClose = true;
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);

                player.openInventory(kitInventory.kits);
            }

            /*if (startCountdown.getTime() == 5) {
                player.closeInventory();
            }*/

            if (startCountdown.getTime() <= 20 && startCountdown.getTime() > 3) {
                player.sendTitle(HexColor.PURPLE + "Starting in",
                        ChatColor.WHITE.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);
            }

            if (startCountdown.getTime() == 3) {
                player.sendTitle(HexColor.PURPLE + "Starting in",
                        HexColor.RED.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() == 2) {
                player.sendTitle(HexColor.PURPLE + "Starting in",
                        HexColor.YELLOW.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() == 1) {
                player.sendTitle(HexColor.PURPLE + "Starting in",
                        HexColor.GREEN.toString() + ChatColor.BOLD + "» " + startCountdown.getTime() + " «"
                        , 0, 25, 0);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.teleport(startLocation);
                player.setGameMode(GameMode.SURVIVAL);

                if (BonusHearts.active) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                            1800 * 20, 2));
                }

                player.sendMessage(HexColor.YELLOW.toString() + ChatColor.BOLD + "\nRACE TO VICTORY");
                player.sendMessage(ChatColor.GRAY + "Whoever reaches "
                        + ChatColor.WHITE + PointsListener.threshold + " Points"
                        + ChatColor.GRAY + " first wins.");
                player.sendMessage(ChatColor.GRAY + "\nUse " + HexColor.YELLOW + "/top"
                        + ChatColor.GRAY + " to teleport to the surface");
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
        bossBar.setTitle(ChatColor.WHITE + "Grace Period ends in " + HexColor.YELLOW
                + ChatColor.BOLD + gracePeriodCountdown.getTime() + ChatColor.WHITE + " seconds");
        bossBar.setProgress(time / 31D);
        bossBar.setVisible(true);

        if (gracePeriodCountdown.getTime() == 0) {
            PlayerDamageListener.gracePeriodActive = false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (gracePeriodCountdown.getTime() == 30) {
                bossBar.addPlayer(player);
            }

            if ((gracePeriodCountdown.getTime() % 10 == 0 && gracePeriodCountdown.getTime() != 0) ||
                    (gracePeriodCountdown.getTime() <= 5 && gracePeriodCountdown.getTime() > 0)) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.sendMessage(ChatColor.GRAY + "The Grace Period ends in "
                        + HexColor.YELLOW + gracePeriodCountdown.getTime() + ChatColor.GRAY + " seconds.");
            }

            if (gracePeriodCountdown.getTime() == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.sendMessage(HexColor.GREEN + "The Grace Period has ended.");
                bossBar.removePlayer(player);
                bossBar.setVisible(false);
            }
        }
    }
}
