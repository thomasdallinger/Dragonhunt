package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.arrows.Arrows;
import com.tharox.dragonhunt.dimensions.DimensionListener;
import com.tharox.dragonhunt.grappler.Grappler;
import com.tharox.dragonhunt.gear.*;
import com.tharox.dragonhunt.spectate.Spectate;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class GameStart {
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

        configureOverworld();
        configurePlayer();
        upgradeRegistration();

        //randomly choose one nether dungeon location
        int rnd = (int) (Math.random() * 1 + 1);
        Location loc = GameState.dungeonLocations.get(rnd);
        Bukkit.getPluginManager().registerEvents(new DimensionListener(loc), Main.getInstance());
        GameState.dungeonLocation = loc;
    }

    private void configureOverworld() {
        overworld.setDifficulty(Difficulty.NORMAL);
        overworld.setTime(0);
        highestBlock = overworld.getHighestBlockAt(0, 0).getLocation();
        WorldBorder worldBorder = overworld.getWorldBorder();
        worldBorder.setCenter(new Location(overworld, 0, 0, 0));
        worldBorder.setSize(1000);
        worldBorder.setDamageAmount(0);
    }

    private void configurePlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
            player.setFoodLevel(20);
            player.setSaturation(4); //players shouldn't loose hunger points instantly
            player.setExp(0);     //resets Bar
            player.setLevel(0);   //resets Number
            player.getInventory().clear();

            player.getInventory().setItem(7, Main.getInstance().getTracker().givePlayerTracker(player));
            new Grappler(player);
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

            Spectate.spectatesLeft.put(player, 3);
            Spectate.spectateCooldown.put(player, timer.getTime() + 180);
            GameState.food.put(player, new ItemStack(Material.RABBIT));

            //set default tracker values
            Main.getInstance().getTracker().playerTracksPlayer.put(player, null);
            Main.getInstance().getTracker().playerTracksIndex.put(player, 0);
        }
    }

    //set all upgrades to 1 so the first level of each upgrade can be purchased
    private void upgradeRegistration() {
        for(Player player : Bukkit.getOnlinePlayers()) {
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
            ToolUpgrade.Unbreaking.put(player, 1);
            ArmorUpgrade.Protection.put(player, 1);
            ArmorUpgrade.Thorns.put(player, 1);
            ArmorUpgrade.FeatherFalling.put(player, 1);
            ArmorUpgrade.DepthStrider.put(player, 1);
            ArmorUpgrade.Respiration.put(player, 1);
            ArmorUpgrade.AquaAffinity.put(player, 1);
            FoodUpgrade.FoodSource.put(player, 1);
            FoodUpgrade.FoodAmount.put(player, 1);
        }
    }

    /**
     * Executed each second (see Countdown.java)
     *
     * @param time time of the countdown
     */
    public void onCountdown(int time) {
        if (overworld == null) return;

        if (startCountdown.getTime() == 0) {
            timer.setRunning(true);
            scoreboardCountdown.setRunning(true);
            Main.elytraInInventory();

            elytraCountdown = new Countdown(true, 30, this::onElytraCountdown);
            overworld.setGameRule(GameRule.FALL_DAMAGE, false);
            bossBar = Bukkit.createBossBar(ChatColor.GREEN + "Fall damage active/Elytra gone in: " + elytraCountdown.getTime(),
                    BarColor.GREEN, BarStyle.SEGMENTED_10);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (startCountdown.getTime() % 10 == 0 && startCountdown.getTime() > 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }
            if (startCountdown.getTime() <= 5 && startCountdown.getTime() > 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }

            if(startCountdown.getTime() > 5)
                player.sendTitle(ChatColor.GOLD.toString() + ChatColor.BOLD + startCountdown.getTime(),
                        "", 0, 20, 0);
            else if (startCountdown.getTime() <= 5 && startCountdown.getTime() > 0)
                player.sendTitle(ChatColor.RED.toString() + ChatColor.BOLD + startCountdown.getTime(),
                        "", 0, 20, 0);
            else
                player.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "GO",
                        "", 0, 20, 0);

            if (startCountdown.getTime() == 0) {
                if(startLocation.getY() - highestBlock.getY() <= 40) {
                    player.teleport(higherStartLocation);
                } else {
                    player.teleport(startLocation);
                }

                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Main.getPrefix() + ChatColor.GOLD + "Good Luck!");
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
        bossBar.setTitle(ChatColor.GREEN + "Fall damage active/Elytra gone in: " + elytraCountdown.getTime());
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
            }

            if (elytraCountdown.getTime() == 0) {
                bossBar.removePlayer(player);
                bossBar.setVisible(false);
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                new Arrows(); //start arrow generation
            }
        }
    }
}
