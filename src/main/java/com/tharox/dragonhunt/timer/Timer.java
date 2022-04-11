package com.tharox.dragonhunt.timer;

import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

            //If the player has his Tracker in his main hand, the time wont get displayed
            ItemStack item = player.getInventory().getItemInMainHand();

            if(item.getItemMeta() != null)
                if(item.getItemMeta().getDisplayName().equals((ChatColor.GOLD + player.getName() + "'s Tracker"))) continue;

            if(player.getGameMode().equals(GameMode.SPECTATOR)) continue;

            if(isRunning()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() +
                        ChatColor.BOLD + DbValues.getTimestamp()));
                continue;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED +
                    "Game starting soon"));
        }

    }

    protected void run() {
        new BukkitRunnable() {
            @Override
            public void run() {

                sendActionBar();

                if(!isRunning()) {
                    return;
                }

                setTime(getTime() + 1);

                //once the timer hits 1h 30min the game will end and nobody won
                if(getTime() == 5400) {
                    new GameEnd();
                    setRunning(false);
                }

            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}
