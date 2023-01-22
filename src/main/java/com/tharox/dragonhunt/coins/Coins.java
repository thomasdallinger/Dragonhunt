package com.tharox.dragonhunt.coins;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.shop.ShopInventory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Statement;
import java.util.HashMap;

/**
 * Used for/in: LevelUp, PlayerInteractListener, BlockBreakListener, EntityDeathListener, PlayerDeathListener,
 * GameStart, UpgradeListener, ResetScoreboardCountdown, AdvancementListener, VehicleListener
 * Coins of each player are displayed in the Scoreboard.
 * Note: Coal ores do not drop XP.
 */
public class Coins {
    // the amount of XP given to the player can be upgraded using XP UPGRADES
    public static int bonusXP = 9; // XP that players would not normally receive
    // from Enderchests, Minecarts, Scoreboard countdown, Advancements (Nether & End entry)
    public static int playerXP = 11; // PlayerDeathListener
    public static int oreXP = 3; // BlockBreakListener
    public static int animalXP = 2; // EntityDeathListener
    public static int monsterXP = 4; // EntityDeathListener

    private static final HashMap<Player, Integer> coins = new HashMap<>();

    public static int getCoins(Player player) {
        return coins.get(player);
    }

    public static void setCoins(Player player, int coinsToSet) {
        coins.put(player, coinsToSet);
    }

    // used in: LevelUp, Timer (last 10 minutes), ResetScoreboardCountdown
    public static void addCoins(Player player, int coinsToAdd) {
        coins.put(player, coins.get(player) + coinsToAdd);
        player.sendMessage(Prefixes.getCoinPrefix() + HexColor.GREEN + "+" + coinsToAdd + ChatColor.GRAY + " Coins");
    }

    public static void removeCoins(Player player, int coinsToRemove, String objectName) {
        coins.put(player, coins.get(player) - coinsToRemove);
        player.sendMessage(Prefixes.getCoinPrefix() + HexColor.RED + "-" + coinsToRemove + ChatColor.GRAY + " Coins");

        // add ShopAccess
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO ShopAccess (GameID, Player, purchasedObject, coinsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + objectName.substring(4)
                    + "', '" + Coins.getCoins(player) + "', '" + ShopInventory.objectCost.get(objectName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
