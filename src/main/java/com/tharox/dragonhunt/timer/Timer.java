package com.tharox.dragonhunt.timer;

import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

            //If the player has his Tracker or his Kit Ability in his main hand, the time wont get displayed
            ItemStack item = player.getInventory().getItemInMainHand();

            if(item.getItemMeta() != null) {
                if(item.getItemMeta().getDisplayName().equals((ChatColor.GOLD + player.getName() + "'s Tracker"))
                        || item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Kit Ability " + player.getName())) continue;
            }

            String hours = "";
            String minutes = "";
            String seconds = "";
            if (getTime() / 3600 < 10)
                hours = "0";
            hours += getTime() / 3600;
            if ((getTime() / 60) % 60 < 10)
                minutes = "0";
            minutes += (getTime() / 60) % 60;
            if (getTime() % 60 < 10)
                seconds = "0";
            seconds += getTime() % 60;

            if(isRunning()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() +
                        ChatColor.BOLD + hours + ":" + minutes + ":" + seconds));
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

                //once the timer hits 2h the game will end and nobody won
                if(getTime() == 7200) {
                    new GameEnd();
                    setRunning(false);
                }

            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}
