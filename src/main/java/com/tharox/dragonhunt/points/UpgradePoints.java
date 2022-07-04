package com.tharox.dragonhunt.points;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Used for/in: LevelUp, PlayerInteractListener, BlockBreakListener, EntityDeathListener, PlayerDeathListener,
 * GameStart, UpgradeListener, ResetScoreboardCountdown, AdvancementListener, VehicleListener
 * Upgrade Points of each player are displayed in the Scoreboard.
 * Coal ores award natural XP (0-2 xp points), which cannot be upgraded using XP Upgrades.
 */
public class UpgradePoints {
    // the amount of XP given to the player can be upgraded using XP UPGRADES (excludes coal ore)
    public static int bonusXP = 9; // XP that players would not normally receive
    // from Enderchests, Minecarts, Scoreboard countdown, Advancements (Nether & End entry)
    public static int playerXP = 11; // PlayerDeathListener
    public static int oreXP = 3; // BlockBreakListener
    public static int animalXP = 1; // EntityDeathListener
    public static int monsterXP = 4; // EntityDeathListener

    private static final HashMap<Player, Integer> upgradePoints = new HashMap<>();

    public static int getPoints(Player player) {
        return upgradePoints.get(player);
    }

    public static void setPoints(Player player, int pointsToSet) {
        upgradePoints.put(player, pointsToSet);
    }

    public static void addPoints(Player player, int pointsToAdd) {
        upgradePoints.put(player, upgradePoints.get(player) + pointsToAdd);
        player.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "+" + pointsToAdd
                + ChatColor.LIGHT_PURPLE + " Upgrade Point");
    }

    public static void removePoints(Player player, int pointsToRemove) {
        upgradePoints.put(player, upgradePoints.get(player) - pointsToRemove);
        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "-" + pointsToRemove
                + ChatColor.RED + " Upgrade Point(s)");
    }
}
