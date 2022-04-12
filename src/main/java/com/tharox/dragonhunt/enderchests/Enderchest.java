package com.tharox.dragonhunt.enderchests;

import com.tharox.dragonhunt.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class Enderchest {
    World world = Bukkit.getServer().getWorld("world");
    Location location;
    int randomAmount;

    public Enderchest() {
        this.location = pickRandomLocation();
        this.randomAmount = pickRandomAmount();

        for (int amount = 1; amount <= randomAmount; amount++) {
            world.getBlockAt(new Location(world, location.getX(), location.getY() + amount, location.getZ()))
                    .setType(Material.ENDER_CHEST);
        }

        world.strikeLightningEffect(location);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Main.getPrefix() + ChatColor.DARK_AQUA + ChatColor.BOLD  + randomAmount +
                    ChatColor.AQUA + " Enderchest(s) spawned at " +
                    ChatColor.DARK_AQUA + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + ".");
            player.sendMessage(ChatColor.AQUA.toString() + ChatColor.ITALIC + (int) location.distance(player.getLocation()) + " Blocks away.");
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
        }
    }

    private int pickRandomAmount() {
        int max = 3, min = 1;
        int range = max - min + 1;

        return (int) (Math.random() * range) + min;
    }

    private Location pickRandomLocation() {
        int max = 500, min = -500;
        int range = max - min + 1;
        int x = (int) (Math.random() * range) + min;
        int z = (int) (Math.random() * range) + min;

        return new Location(world, x, world.getHighestBlockYAt(x, z), z);
    }
}
