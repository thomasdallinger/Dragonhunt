package com.tharox.dragonhunt.chat;

import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Prefixes {

    // IMPORTANT: used for important messages
    public static String getImportantPrefix() {
        return ChatColor.DARK_GRAY + "[" + HexColor.RED + ChatColor.BOLD + "!!!"
                + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // XP: used for messages related to XP gains
    public static String getXPPrefix(int xp) {
        return ChatColor.DARK_GRAY + "[" + HexColor.GREEN + "+" + xp + ChatColor.WHITE + " XP"
                + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // COINS: used for messages related to coins
    public static String getCoinPrefix(int coins) {
        if (coins > 0)
            return ChatColor.DARK_GRAY + "[" + HexColor.GREEN + "+" + coins + ChatColor.WHITE + " ⛃"
                    + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
        else
            return ChatColor.DARK_GRAY + "[" + HexColor.RED + coins + ChatColor.WHITE + " ⛃"
                    + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // TRACKING: used for messages related to the tracker
    public static String getTrackingPrefix() {
        return ChatColor.DARK_GRAY + "[" + HexColor.YELLOW + "◄ ►" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // MODIFIER: used for messages related to modifiers
    public static String getModifierPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + ChatColor.BOLD + "▲▼▲" + ChatColor.DARK_GRAY + "] "
                + ChatColor.GRAY;
    }

    // ADVANCEMENT: used for messages related to advancements
    public static String getAdvancementPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "✦✦✦" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // ERROR: when a player is not allowed to do something or is doing something wrong
    public static String getErrorPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "✖✖✖" + ChatColor.DARK_GRAY + "] " + ChatColor.RED;
    }

    // DEATH: when a player dies
    public static String getDeathPrefix() {
        return ChatColor.DARK_GRAY + "[" + HexColor.RED + "☠☠☠" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // LEAVE: when a player leaves
    public static String getLeavePrefix() {
        return ChatColor.DARK_GRAY + "[" + HexColor.RED + "«««" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    }

    // NAME: used for displaying a players kit in chat and the tablist next to a player's name
    public static String getNamePrefix(Player player) {
        if (GameState.selectedKit.containsKey(player)) {
            return ChatColor.DARK_GRAY + "[" + GameState.selectedKit.get(player) + ChatColor.DARK_GRAY + "] ";
        } else {
            return "";
        }
    }
}
