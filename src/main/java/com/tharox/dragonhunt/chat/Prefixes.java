package com.tharox.dragonhunt.chat;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Prefixes {

    // ALL: used for messages displayed to all players
    public static String getAllPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + ChatColor.BOLD + "»»" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // XP: used for messages related to XP gains
    public static String getXPPrefix(int xp) {
        return ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "+" + xp + HexColor.GREEN + " XP" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // COINS: used for messages related to coins
    public static String getCoinPrefix(int coins) {
        if(coins > 0)
            return ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "+" + coins + HexColor.GREEN + " ⛃" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
        else
            return ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + coins + HexColor.RED + " ⛃" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // TRACKING: used for messages related to the tracker or positions
    public static String getTrackingPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "↑↑↑" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // ERROR: when a player is not allowed to do something or is doing something wrong
    public static String getErrorPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "✖✖✖" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // DEATH: when a player dies
    public static String getDeathPrefix() {
        return ChatColor.DARK_GRAY + "[" + HexColor.RED + "☠☠☠" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // LEAVE: when a player leaves
    public static String getLeavePrefix() {
        return ChatColor.DARK_GRAY + "[" + HexColor.RED + "«««" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // NAME: used for statistics in chat and the tablist next to a player's name
    public static String getNamePrefix(Player player) {
        return ChatColor.DARK_GRAY + "["
                + ChatColor.WHITE + "⚔ " + ChatColor.AQUA + "Kitname"
                + ChatColor.WHITE + " ✦ " + HexColor.HIGHLIGHT + player.getLevel()
                + ChatColor.WHITE + " ☠ " + ChatColor.GOLD + DbValues.getKills(player, true)
                + ChatColor.DARK_GRAY + "] ";
    }
}
