package com.tharox.dragonhunt.chat;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Prefixes {

    // ALL: used for messages displayed to all players
    public static String getAllPrefix() {
        return ChatColor.WHITE + "☻" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
    }

    // COINS: used for messages related to coins and the shop
    public static String getCoinPrefix() {
        return HexColor.HIGHLIGHT + "⛃" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
    }

    // TRACKING: used for messages related to the tracker or positions
    public static String getTrackingPrefix() {
        return ChatColor.DARK_GREEN + "→" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
    }

    // ERROR: when a player is not allowed to do something
    public static String getErrorPrefix() {
        return ChatColor.DARK_RED + "✖" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
    }

    // DEATH: when a player dies
    public static String getDeathPrefix() {
        return HexColor.RED + "☠" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
    }

    // KILL: when a player gets killed by another player
    public static String getKillPrefix() {
        return ChatColor.GOLD + "☠" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
    }

    // LEAVE: when a player leaves
    public static String getLeavePrefix() {
        return HexColor.RED + "«" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY;
    }

    // NAME: used for statistics in chat and the tablist next to a player's name
    public static String getNamePrefix(Player player) {
        return ChatColor.WHITE.toString() + "☠ "
                + ChatColor.GOLD + DbValues.getKills(player, true)
                + ChatColor.WHITE + " ⛃ "
                + HexColor.HIGHLIGHT + Coins.getCoins(player)
                + ChatColor.DARK_GRAY + " | ";
    }
}
