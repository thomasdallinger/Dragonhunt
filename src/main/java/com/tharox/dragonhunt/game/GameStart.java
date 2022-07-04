package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.arrows.Arrows;
import com.tharox.dragonhunt.upgrades.*;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GameStart extends GameConfiguration {
    Timer timer = Main.getInstance().getTimer();
    Countdown startCountdown;
    Countdown elytraCountdown;
    Countdown scoreboardCountdown = Main.getInstance().getScoreboardCountdown();
    BossBar bossBar;
    World overworld = Bukkit.getServer().getWorld("world");
    Location startLocation = new Location(overworld, 0, 150, 0);
    Location highestBlock;
    Location higherStartLocation = new Location(startLocation.getWorld(), startLocation.getX(), startLocation.getY() + 40, startLocation.getZ());

    public GameStart() {
        startCountdown = new Countdown(true, 20, this::onCountdown);
        highestBlock = overworld.getHighestBlockAt(0, 0).getLocation();

        configureOverworld();
        configureNether();
        configurePlayer();
        upgradeRegistration();
    }

    /**
     * Sets the access level of all available UPGRADES to 1.
     * This means that each level 1 UPGRADE can be purchased from the UPGRADE inventory (/upgrades).
     */
    private void upgradeRegistration() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            WeaponUpgrade.Sharpness.put(player, 1);
            WeaponUpgrade.Knockback.put(player, 1);
            WeaponUpgrade.FireAspect.put(player, 1);
            BowUpgrade.Power.put(player, 1);
            BowUpgrade.Punch.put(player, 1);
            BowUpgrade.Flame.put(player, 1);
            CrossbowUpgrade.Piercing.put(player, 1);
            CrossbowUpgrade.Multishot.put(player, 1);
            CrossbowUpgrade.QuickCharge.put(player, 1);
            ArrowUpgrade.MoreArrows.put(player, 1);
            ArrowUpgrade.EffectArrows.put(player, 1);
            ToolUpgrade.Efficiency.put(player, 1);
            ToolUpgrade.Unbreakable.put(player, 1);
            ArmorUpgrade.Protection.put(player, 1);
            ArmorUpgrade.Thorns.put(player, 1);
            ArmorUpgrade.FeatherFalling.put(player, 1);
            ArmorUpgrade.DepthStrider.put(player, 1);
            ArmorUpgrade.Respiration.put(player, 1);
            ArmorUpgrade.AquaAffinity.put(player, 1);
            FoodUpgrade.FoodSource.put(player, 1);
            FoodUpgrade.FoodAmount.put(player, 1);
            ResourceUpgrade.SmeltedResource.put(player, 1);
            ResourceUpgrade.ResourceAmount.put(player, 1);
            XPUpgrade.MoreXP.put(player, 1);
        }
    }

    /**
     * Countdown which is executed each second (see Countdown.java).
     *
     * @param time time of the countdown
     */
    public void onCountdown(int time) {
        if (overworld == null) return;

        // once the countdown hits zero, start the TIMER and SCOREBOARD COUNTDOWN
        if (startCountdown.getTime() == 0) {
            timer.setRunning(true);
            scoreboardCountdown.setRunning(true);
            Main.elytraInInventory();

            elytraCountdown = new Countdown(true, 30, this::onElytraCountdown);
            overworld.setGameRule(GameRule.FALL_DAMAGE, false);
            bossBar = Bukkit.createBossBar(ChatColor.GREEN + "Elytra removed in " + ChatColor.DARK_GREEN
                    + ChatColor.BOLD + elytraCountdown.getTime() + ChatColor.GREEN + " seconds", BarColor.GREEN, BarStyle.SEGMENTED_10);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (startCountdown.getTime() % 10 == 0 && startCountdown.getTime() > 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }
            if (startCountdown.getTime() <= 5 && startCountdown.getTime() > 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if (startCountdown.getTime() > 5)
                player.sendTitle(ChatColor.GOLD.toString() + ChatColor.BOLD + startCountdown.getTime(),
                        "", 0, 20, 0);
            else if (startCountdown.getTime() <= 5 && startCountdown.getTime() > 0)
                player.sendTitle(ChatColor.RED.toString() + ChatColor.BOLD + startCountdown.getTime(),
                        "", 0, 20, 0);
            else
                player.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "GO",
                        "", 0, 20, 0);

            if (startCountdown.getTime() == 0) {
                if (startLocation.getY() - highestBlock.getY() <= 40) {
                    player.teleport(higherStartLocation);
                } else {
                    player.teleport(startLocation);
                }

                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Main.getPrefix() + ChatColor.GOLD + "Whoever kills the Enderdragon wins.");
                player.sendMessage(Main.getPrefix() + ChatColor.RED + "Fall damage is disabled!");
                player.sendMessage(Main.getPrefix() + ChatColor.GREEN + "Mark positions using "
                        + ChatColor.DARK_GREEN + "/pos [name]" + ChatColor.GREEN + ".");
                player.sendMessage(Main.getPrefix() + ChatColor.GREEN + "List your created positions using "
                        + ChatColor.DARK_GREEN + "/pos" + ChatColor.GREEN + ".");
            }
        }
    }

    /**
     * Executed after the timer starts.
     * 30s BossBar
     *
     * @param time time of the countdown
     */
    public void onElytraCountdown(int time) {
        bossBar.setTitle(ChatColor.GREEN + "Elytra removed in " + ChatColor.DARK_GREEN
                + ChatColor.BOLD + elytraCountdown.getTime() + ChatColor.GREEN + " seconds");
        bossBar.setProgress(time / 31D);
        bossBar.setVisible(true);

        if (elytraCountdown.getTime() == 0) {
            overworld.setGameRule(GameRule.FALL_DAMAGE, true);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (elytraCountdown.getTime() == 30) {
                bossBar.addPlayer(player);
            }

            if (elytraCountdown.getTime() <= 5 && elytraCountdown.getTime() > 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "Elytra removed in "
                        + ChatColor.GOLD + elytraCountdown.getTime() + ChatColor.YELLOW + " seconds.");
            }

            if (elytraCountdown.getTime() == 0) {
                player.sendMessage(Main.getPrefix() + ChatColor.GREEN + "Fall damage is enabled!");
                player.sendMessage(Main.getPrefix() + ChatColor.GOLD + "Spectates (/spec) " +
                        ChatColor.YELLOW + "are now usable.");
                bossBar.removePlayer(player);
                bossBar.setVisible(false);
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                new Arrows(); //start arrow generation
            }
        }
    }
}
