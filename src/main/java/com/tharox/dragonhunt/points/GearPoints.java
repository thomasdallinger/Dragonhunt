package com.tharox.dragonhunt.points;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Used for/in: LevelUp, PlayerInteractListener, BlockBreakListener, EntityDeathListener, PlayerDeathListener, GameStart, GearUpgradeListener, ResetScoreboardCountdown
 * Coal ores award natural XP (0-2 xp points), which cannot be upgraded using XP Upgrades.
 */
public class GearPoints {
    public static int enderchestXP = 15; //PlayerInteractListener
    public static int playerXP = 12; //PlayerDeathListener
    public static int oreXP = 1; //BlockBreakListener
    public static int animalXP = 1; //EntityDeathListener
    public static int monsterXP = 5; //EntityDeathListener

    private static final HashMap<Player, Integer> gearPoints = new HashMap<>();

    public static int getPoints(Player player) {
        return gearPoints.get(player);
    }

    public static void setPoints(Player player, int pointsToSet) {
        gearPoints.put(player, pointsToSet);
    }

    public static void addPoints(Player player, int pointsToAdd) {
        gearPoints.put(player, gearPoints.get(player) + pointsToAdd);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.sendTitle(ChatColor.AQUA + " ", ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "+"
                + pointsToAdd + ChatColor.LIGHT_PURPLE + " Gear point(s)", 5, 20, 5);

        //TODO: new DB implementation
    }

    public static void removePoints(Player player, int pointsToRemove) {
        gearPoints.put(player, gearPoints.get(player) - pointsToRemove);

        player.sendMessage(ChatColor.WHITE + "Points left: " + ChatColor.GOLD
                + GearPoints.getPoints(player) + ChatColor.RED + " (-" + pointsToRemove + ")");
    }
}
