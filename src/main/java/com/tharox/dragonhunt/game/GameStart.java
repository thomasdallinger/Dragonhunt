package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.arrows.Arrows;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.shop.Effects;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.Enchantments;
import com.tharox.dragonhunt.kits.selection.KitInventory;
import com.tharox.dragonhunt.modifiers.classes.BonusHearts;
import com.tharox.dragonhunt.modifiers.selection.ModifierInventory;
import com.tharox.dragonhunt.movement.ElytraSwapRunnable;
import com.tharox.dragonhunt.players.PlayerDamageListener;
import com.tharox.dragonhunt.players.PlayerEndTeleport;
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

import java.util.UUID;

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
        startCountdown = new Countdown(true, 40, this::onCountdown);
        modifierInventory = new ModifierInventory(true);
        highestY = overworld.getHighestBlockYAt((int) x, (int) z);
        startLocation = new Location(overworld, x, highestY + 55, z);

        configureOverworld(x, z);
        configureNether();
        configureEnd();
        configurePlayer(startLocation);

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

            Arrows.initialize(); // start arrow generation
            new ElytraSwapRunnable().runTaskTimer(Main.getInstance(), 0, 5);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (startCountdown.getTime() == 40) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.openInventory(modifierInventory.modifiers);

                // Game officially started - elo match entry costs now come into effect
                // If a player quits past this point elo is deducted
                int elo = DbValues.getElo(player);
                int entryCost;

                if (elo < 200) {
                    entryCost = 10;
                } else if (elo < 400) {
                    entryCost = 30;
                } else if (elo < 600) {
                    entryCost = 50;
                } else if (elo < 800) {
                    entryCost = 70;
                } else {
                    entryCost = 90;
                }

                GameState.eloPerPlayer.put(player, -entryCost);
            }

            if (startCountdown.getTime() == 25) {
                GameState.modifierInventoryClose = true;
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() == 20) {
                // Each player has a different kits inventory due to kit unlocks.
                KitInventory kitInventory = new KitInventory(player);
                player.openInventory(kitInventory.kits);
            }

            if (startCountdown.getTime() <= 25 && startCountdown.getTime() > 3) {
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
                player.setGameMode(GameMode.SURVIVAL);

                // Allow players to fly normally in Spectator Mode from now.
                GameState.flySpeedZero.put(player, false);

                if (BonusHearts.active) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,
                            1800 * 20, 2));
                }

                player.sendMessage(HexColor.YELLOW.toString() + ChatColor.BOLD + "\nRACE TO VICTORY");
                player.sendMessage(ChatColor.GRAY + "Whoever deals "
                        + ChatColor.WHITE + PointsListener.threshold + " Damage"
                        + ChatColor.GRAY + " to the Ender Dragon first wins.");
                player.sendMessage(ChatColor.GRAY + "You need " + ChatColor.WHITE + PlayerEndTeleport.getEyesNeeded()
                        + " Eyes of Ender " + ChatColor.GRAY + "to receive an" + ChatColor.WHITE + " End Teleporter"
                        + ChatColor.GRAY + ".");

                player.sendMessage(ChatColor.GRAY + "\nUse " + HexColor.YELLOW + "/shop"
                        + ChatColor.GRAY + " to purchase enchantments and effects.");
                player.sendMessage(ChatColor.GRAY + "Use " + HexColor.YELLOW + "/top"
                        + ChatColor.GRAY + " to teleport to the surface.");
                player.sendMessage(ChatColor.GRAY + "Use " + HexColor.YELLOW + "/scout"
                        + ChatColor.GRAY + " to go into " + ChatColor.WHITE + "Spectator Mode"
                        + ChatColor.GRAY + " for a short time.");
                player.sendMessage(ChatColor.GRAY + "Use " + HexColor.YELLOW + "/modifiers"
                        + ChatColor.GRAY + " to see the active modifiers.");
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

            if ((gracePeriodCountdown.getTime() % 10 == 0 && gracePeriodCountdown.getTime() != 0
                    && gracePeriodCountdown.getTime() != 30) ||
                    (gracePeriodCountdown.getTime() <= 3 && gracePeriodCountdown.getTime() > 0)) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.sendMessage(ChatColor.GRAY + "The Grace Period ends in "
                        + HexColor.YELLOW + gracePeriodCountdown.getTime() + ChatColor.GRAY + " seconds.");
            }

            if (gracePeriodCountdown.getTime() == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                player.sendMessage(HexColor.GREEN + "The Grace Period has ended.");
                bossBar.removePlayer(player);
                bossBar.setVisible(false);

                // Add the Points Boss Bar to all players
                UUID id = player.getUniqueId();

                PointsListener.bossBars.put(id, Bukkit.createBossBar(
                        HexColor.YELLOW.toString() + ChatColor.BOLD + PointsListener.scorePerPlayer.get(id)
                                + ChatColor.WHITE + " / " + PointsListener.threshold + " Ender Dragon Damage",
                        BarColor.YELLOW, BarStyle.SOLID));
                PointsListener.bossBars.get(id).addPlayer(player);
                PointsListener.bossBars.get(id).setProgress(
                        PointsListener.scorePerPlayer.get(id) / (PointsListener.threshold + 1D));
                PointsListener.bossBars.get(id).setVisible(true);
            }
        }
    }
}
