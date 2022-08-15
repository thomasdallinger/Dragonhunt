package com.tharox.dragonhunt.timer;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameEnd;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private boolean running;
    private int time;

    public Timer(boolean running, int time) {
        this.running = running;
        this.time = time;

        run();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void sendActionBar() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            // if the player has his Tracker in his main hand, the time wont get displayed
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getItemMeta() != null)
                if (item.getItemMeta().getDisplayName().equals((ChatColor.GOLD.toString()
                        + ChatColor.BOLD + "Tracker")))
                    continue;

            // if the player is in Spectator mode, the time wont get displayed (Spectate or after the game ended)
            if (player.getGameMode().equals(GameMode.SPECTATOR)) continue;

            // display the time
            // if the showdown is active, the time will get displayed in red
            ChatColor color = HexColor.HIGHLIGHT;

            if (getTime() >= 2400) {
                color = HexColor.RED;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new ComponentBuilder(DbValues.getTimestamp()).color(color).bold(true).create());
        }
    }

    protected void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) {
                    return;
                }

                setTime(getTime() + 1);
                sendActionBar();
                Main.getInstance().getTablistManager().setAllPlayerTeams();

                if (getTime() >= 2395 && getTime() < 2400)
                    showdownCountdown();

                if (getTime() % 600 == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (getTime() < 2400)
                            player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "All Players will receive "
                                    + ChatColor.YELLOW + "Strength II" + ChatColor.GRAY + ", "
                                    + ChatColor.YELLOW + "Speed II" + ChatColor.GRAY + " and "
                                    + ChatColor.YELLOW + "Resistance II" + ChatColor.GRAY + " in "
                                    + ChatColor.YELLOW + (2400 - getTime()) / 60 + ChatColor.GRAY + " minutes.");
                        else if (getTime() == 2400) {
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                            player.getWorld().strikeLightningEffect(player.getLocation());
                            Coins.addCoins(player, 30);

                            player.sendTitle(ChatColor.AQUA + " ", ChatColor.DARK_RED.toString()
                                    + ChatColor.BOLD + "SHOWDOWN", 5, 100, 5);
                            player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "The game ends in "
                                    + ChatColor.YELLOW + "10" + ChatColor.GRAY + " minutes.");
                        }
                    }
                }

                // add effects to each player, if the showdown is active (time >= 40 minutes)
                if (getTime() >= 2400 && getTime() < 3000) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.INCREASE_DAMAGE, 40, 1));
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.SPEED, 40, 1));
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.DAMAGE_RESISTANCE, 40, 1));
                    }
                }

                // once the timer hits 50 minutes, the game will end without a winner
                if (getTime() == 3000) {
                    new GameEnd();
                    setRunning(false);
                }
            }
        }.runTaskTimer(Main.getInstance(), 40, 20);
    }

    private void showdownCountdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            player.sendMessage(Prefixes.getAllPrefix() + ChatColor.GRAY + "Showdown starts in "
                    + ChatColor.YELLOW + (2400 - getTime()) + ChatColor.GRAY + " seconds.");
        }
    }
}
