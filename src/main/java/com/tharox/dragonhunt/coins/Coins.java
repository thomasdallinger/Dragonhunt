package com.tharox.dragonhunt.coins;

import com.tharox.dragonhunt.chat.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Used for/in: LevelUp, PlayerInteractListener, BlockBreakListener, EntityDeathListener, PlayerDeathListener,
 * GameStart, UpgradeListener, ResetScoreboardCountdown, AdvancementListener, VehicleListener
 * Coins of each player are displayed in the Scoreboard.
 * Note: Coal ores do not drop XP.
 */
public class Coins {
    public static int advancementXP = 9; // XP awarded for advancements
    public static int playerXP = 12; // XP awarded for killing players
    public static int chestXP = 6; // XP awarded for opening chests and minecarts
    public static int oreXP = 3; // XP awarded for mining ores that are not dropping resources
    public static int animalXP = 2; // XP awarded for killing animals
    public static int monsterXP = 4; // XP awarded for killing monsters

    private static final HashMap<Player, Integer> coins = new HashMap<>();

    public static int getCoins(Player player) {
        return coins.get(player);
    }

    public static void setCoins(Player player, int coinsToSet) {
        coins.put(player, coinsToSet);
    }

    // used in: LevelUp, Timer (last 10 minutes)
    public static void addCoins(Player player, int coinsToAdd) {
        coins.put(player, coins.get(player) + coinsToAdd);
    }

    public static void removeCoins(Player player, int coinsToRemove) {
        coins.put(player, coins.get(player) - coinsToRemove);
    }

    public static void addXP(Player player, int xpToAdd) {
        player.giveExp(xpToAdd);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        int currentXPPoints = (int) (player.getExpToLevel() * (player.getExp() * 100) / 100);
        player.sendTitle("", HexColor.GREEN + "+" + (xpToAdd) + ChatColor.WHITE + " XP "
                        + ChatColor.DARK_GRAY + "[" + currentXPPoints + "/" + player.getExpToLevel() + "]",
                0, 30, 10);
    }
}
