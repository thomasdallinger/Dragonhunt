package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.dimensions.DimensionListener;
import com.tharox.dragonhunt.kits.KitInventory;
import com.tharox.dragonhunt.points.Points;
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
    KitInventory kitInventory = new KitInventory();
    BossBar bossBar;
    World overworld = Bukkit.getServer().getWorld("world");
    Location startLocation = new Location(overworld, 0, 150, 0);
    Location highestBlock;
    Location higherStartLocation = new Location(startLocation.getWorld(), startLocation.getX(), startLocation.getY() + 40, startLocation.getZ());

    public GameStart() {
        startCountdown = new Countdown(true, 30, this::onCountdown);

        configureOverworld();
        configurePlayer();

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
        worldBorder.setSize(10000);
        worldBorder.setDamageAmount(0);
    }

    private void configurePlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
            player.setFoodLevel(20);
            player.setExp(0);     //resets Bar
            player.setLevel(0);   //resets Number
            player.getInventory().clear();

            player.getInventory().setItem(7, Main.getInstance().getTracker().givePlayerTracker(player));
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
            player.getInventory().setItem(0, addItem(new ItemStack(Material.GOLDEN_AXE)));
            player.getInventory().setItem(1, addItem(new ItemStack(Material.GOLDEN_PICKAXE)));
            player.getInventory().setItemInOffHand(addItem(new ItemStack(Material.SHIELD)));

            Spectate.spectatesLeft.put(player, 3);
            Spectate.spectateCooldown.put(player, timer.getTime() + 180);

            //set default tracker values
            Main.getInstance().getTracker().playerTracksPlayer.put(player, null);
            Main.getInstance().getTracker().playerTracksIndex.put(player, 0);
        }
    }

    /**
     * @param item ItemStack to be modified and returned
     * @return ItemStack to be added to the players inventory
     */
    private ItemStack addItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        if (item.getType() == Material.SHIELD) {
            meta.addEnchant(Enchantment.DURABILITY, 2, true);
        } else {
            meta.addEnchant(Enchantment.DIG_SPEED, 2, true);
        }

        item.setItemMeta(meta);
        return item;
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

            player.sendTitle(ChatColor.GOLD.toString() + ChatColor.BOLD + startCountdown.getTime(), "", 10, 20, 10);

            if (startCountdown.getTime() == 20) {
                player.openInventory(kitInventory.kits);
            }

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
            }
        }
    }
}
