package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.chat.HexColor;
import org.bukkit.Bukkit;
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
                    Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix()
                            + HexColor.RED + "World name is incorrect.");
                    return;
                }

                world.setTime(14000);
                world.setStorm(true);
                world.setWeatherDuration(Integer.MAX_VALUE);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
