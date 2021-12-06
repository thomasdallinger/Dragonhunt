package com.tharox.dragonhunt.points;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Used for: PlayerInteractListener, VehicleListener, EntityDeathListener, PlayerDeathListener, GameStart, ShopListener, Scoreboard
 */
public class Points {
    private static final HashMap<Player, Integer> points = new HashMap<>();

    public static int getPoints(Player player) {
        return points.get(player);
    }

    public static void setPoints(Player player, int pointsToSet) {
        points.put(player, pointsToSet);
    }

    public static void addPoints(Player player, int pointsToAdd) {
        points.put(player, points.get(player) + pointsToAdd);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        if(player.getWorld().getName().equals("world"))
            player.sendTitle("", ChatColor.AQUA + "+" + pointsToAdd + " Points", 5, 10, 5);
        else
            player.sendTitle("", ChatColor.LIGHT_PURPLE + "+" + pointsToAdd + " Points", 5, 10, 5);
    }

    public static void removePoints(Player player, int pointsToRemove) {
        points.put(player, points.get(player) - pointsToRemove);

        player.sendMessage(ChatColor.WHITE + "Points left: " + ChatColor.GOLD
                + Points.getPoints(player) + ChatColor.RED + " (-" + pointsToRemove + ")");
    }
}
