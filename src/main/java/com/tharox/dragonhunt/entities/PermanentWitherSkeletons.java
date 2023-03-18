package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.modifiers.classes.WitherSkeletons;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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
                    player.sendMessage(Prefixes.getImportantPrefix() + ChatColor.GRAY + "A "
                            + HexColor.HIGHLIGHT + "Wither Skeleton" + ChatColor.GRAY + " appeared near all players.");
                }

                random = (int) (Math.random() * range) + min;
            }
        }.runTaskTimer(Main.getInstance(), random, random);
    }
}
