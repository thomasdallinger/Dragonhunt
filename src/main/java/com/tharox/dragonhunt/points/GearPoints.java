package com.tharox.dragonhunt.points;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Used for/in: LevelUp, PlayerInteractListener, BlockBreakListener, EntityDeathListener, PlayerDeathListener,
 * GameStart, GearUpgradeListener, ResetScoreboardCountdown
 * Gear Points of each player are displayed in the Scoreboard.
 * Coal ores award natural XP (0-2 xp points), which cannot be upgraded using XP Upgrades.
 */
public class GearPoints {
    // the amount of XP given to the player can be upgraded using XP UPGRADES (excludes coal ore)
    public static int enderchestXP = 12; // PlayerInteractListener, ResetScoreboardCountdown
    public static int playerXP = 10; // PlayerDeathListener
    public static int oreXP = 3; // BlockBreakListener
    public static int animalXP = 1; // EntityDeathListener
    public static int monsterXP = 4; // EntityDeathListener

    private static final HashMap<Player, Integer> gearPoints = new HashMap<>();

    public static int getPoints(Player player) {
        return gearPoints.get(player);
    }

    public static void setPoints(Player player, int pointsToSet) {
        gearPoints.put(player, pointsToSet);
    }

    public static void addPoints(Player player, int pointsToAdd) {
        gearPoints.put(player, gearPoints.get(player) + pointsToAdd);
        player.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "+" + pointsToAdd
                + ChatColor.LIGHT_PURPLE + " Gear Point(s)");
    }

    public static void removePoints(Player player, int pointsToRemove) {
        gearPoints.put(player, gearPoints.get(player) - pointsToRemove);
        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "-" + pointsToRemove
                + ChatColor.RED + " Gear Point(s)");
    }
}
