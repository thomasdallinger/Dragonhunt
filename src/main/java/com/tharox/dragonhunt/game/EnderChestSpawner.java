package com.tharox.dragonhunt.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class EnderChestSpawner {

    public void run() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;
        Location location;
        int straight = -1600;
        int diagonal = -1500;

        while (straight <= 1600) {

            if (straight == 0) {
                straight += 400;
                continue;
            }

            location = new Location(world, straight, world.getHighestBlockYAt(straight, 0) + 1, 0);
            world.getBlockAt(location).setType(Material.ENDER_CHEST);

            location = new Location(world, 0, world.getHighestBlockYAt(straight, 0) + 1, straight);
            world.getBlockAt(location).setType(Material.ENDER_CHEST);

            straight += 400;
        }

        while (diagonal <= 1500) {

            if (diagonal == 0) {
                diagonal += 500;
                continue;
            }

            location = new Location(world, diagonal, world.getHighestBlockYAt(straight, 0) + 1, diagonal);
            world.getBlockAt(location).setType(Material.ENDER_CHEST);

            location = new Location(world, diagonal, world.getHighestBlockYAt(straight, 0) + 1, -diagonal);
            world.getBlockAt(location).setType(Material.ENDER_CHEST);

            diagonal += 500;
        }
    }
}
