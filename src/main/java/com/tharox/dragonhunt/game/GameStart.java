package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.arrows.Arrows;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.elytra.ElytraSwapRunnable;
import com.tharox.dragonhunt.modifiers.classes.EndlessNight;
import com.tharox.dragonhunt.modifiers.selection.ModifierInventory;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import com.tharox.dragonhunt.upgrades.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameStart extends GameConfiguration {
    Timer timer = Main.getInstance().getTimer();
    Countdown startCountdown, elytraCountdown;
    Countdown scoreboardCountdown = Main.getInstance().getScoreboardCountdown();
    ModifierInventory modifierInventory;
    BossBar bossBar;
    World overworld = Bukkit.getServer().getWorld("world");
    Location startLocation = new Location(overworld, 0, 200, 0);
    Location highestBlock;
    // used if the highest block at x: 0 and z: 0 is within 40 blocks of the startLocation
    Location higherStartLocation = new Location(startLocation.getWorld(), startLocation.getX(),
            startLocation.getY() + 40, startLocation.getZ());

    public GameStart() {
        startCountdown = new Countdown(true, 20, this::onCountdown);
        modifierInventory = new ModifierInventory();
        highestBlock = overworld.getHighestBlockAt(0, 0).getLocation();

        configureOverworld();
        configureNether();
        configureEnd();
        configurePlayer();
        upgradeRegistration();
    }

    /**
     * Sets the access level of all available UPGRADES to 1.
     * This means that each level 1 UPGRADE can be purchased from the UPGRADE inventory (/upgrades).
     */
    private void upgradeRegistration() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Weapon
            EnchantmentUpgrades.Sharpness.put(player, 1);
            EnchantmentUpgrades.Knockback.put(player, 1);
            EnchantmentUpgrades.FireAspect.put(player, 1);
            // Bow
            EnchantmentUpgrades.Power.put(player, 1);
            EnchantmentUpgrades.Punch.put(player, 1);
            EnchantmentUpgrades.Flame.put(player, 1);
            // Tool
            EnchantmentUpgrades.Efficiency.put(player, 1);
            EnchantmentUpgrades.Unbreakable.put(player, 1);
            // Armor
            EnchantmentUpgrades.Protection.put(player, 1);
            EnchantmentUpgrades.Thorns.put(player, 1);
            EnchantmentUpgrades.FeatherFalling.put(player, 1);
            // Non-enchantment upgrades
            ArrowUpgrade.MoreArrows.put(player, 1);
            ArrowUpgrade.EffectArrows.put(player, 1);
            FoodUpgrade.FoodSource.put(player, 1);
            FoodUpgrade.FoodAmount.put(player, 1);
            ResourceUpgrade.ResourceAmount.put(player, 1);
            XPUpgrade.MoreXP.put(player, 1);
        }

        new EnchantmentUpgrades(); // start the runnable
        // checks if each player has purchased enchantment upgrades and enchants items based on the upgrade level
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
            new Arrows(); // start arrow generation

            Main.elytraInInventory();
            elytraCountdown = new Countdown(true, 30, this::onElytraCountdown);

            if (!EndlessNight.active)
                overworld.setTime(0);
            else
                new PermanentNight();

            overworld.setGameRule(GameRule.FALL_DAMAGE, false);
            bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_6);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (startCountdown.getTime() == 20) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                player.openInventory(modifierInventory.modifiers);
            }

            if (startCountdown.getTime() == 5) {
                GameState.modifierInventoryClose = true;
                player.closeInventory();
            }

            if (startCountdown.getTime() <= 5 && startCountdown.getTime() > 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);

                if (startCountdown.getTime() > 3)
                    player.sendTitle(ChatColor.GOLD.toString() + ChatColor.BOLD + startCountdown.getTime(),
                            "", 0, 20, 0);
                else if (startCountdown.getTime() <= 3)
                    player.sendTitle(HexColor.RED.toString() + ChatColor.BOLD + startCountdown.getTime(),
                            "", 0, 20, 0);
            }

            if (startCountdown.getTime() == 0) {
                if (startLocation.getY() - highestBlock.getY() <= 40) {
                    player.teleport(higherStartLocation);
                } else {
                    player.teleport(startLocation);
                }

                player.setGameMode(GameMode.SURVIVAL);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0));
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GOLD + ChatColor.BOLD
                        + "Whoever kills the Enderdragon wins.");
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "Fall damage is "
                        + HexColor.RED + "disabled" + ChatColor.GRAY + ".");
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.YELLOW + "/pos [name]" + ChatColor.DARK_GRAY + ": "
                        + ChatColor.GRAY + "mark locations");
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
     * 30s BossBar
     *
     * @param time time of the countdown
     */
    public void onElytraCountdown(int time) {
        bossBar.setTitle(HexColor.RED + "Elytra removed in " + ChatColor.DARK_RED
                + ChatColor.BOLD + elytraCountdown.getTime() + HexColor.RED + " seconds");
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
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "Elytra removed in "
                        + HexColor.RED + elytraCountdown.getTime() + ChatColor.GRAY + " seconds.");
            }

            if (elytraCountdown.getTime() == 0) {
                player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "Fall damage is "
                        + HexColor.GREEN + "enabled" + ChatColor.GRAY + ".");
                bossBar.removePlayer(player);
                bossBar.setVisible(false);
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                new ElytraSwapRunnable().runTaskTimer(Main.getInstance(), 0, 5);
            }
        }
    }
}
