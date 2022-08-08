package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.WitherSkeletons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentWitherSkeletons {
    int random;
    int max = 6000; // 5 minutes
    int min = 2400; // 2 minutes
    int range = max - min + 1;

    public PermanentWitherSkeletons() {
        random = (int) (Math.random() * range) + min;

        if (WitherSkeletons.active) {
            run();
        }
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getWorld().spawnEntity(player.getLocation(), EntityType.WITHER_SKELETON);
                    player.sendMessage(Main.getPrefix() + ChatColor.GRAY + "A Wither Skeleton appeared near you.");
                }

                random = (int) (Math.random() * range) + min;
            }
        }.runTaskTimer(Main.getInstance(), random, random);
    }
}
