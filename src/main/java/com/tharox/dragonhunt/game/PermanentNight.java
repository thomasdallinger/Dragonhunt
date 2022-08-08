package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentNight {

    public PermanentNight() {
        run();
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getServer().getWorld("world");

                if (world == null) {
                    Bukkit.getServer().broadcastMessage(ChatColor.RED + "ERROR: World name is incorrect.");
                    return;
                }

                world.setTime(14000);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
