package com.tharox.dragonhunt.leveling;

import com.tharox.dragonhunt.chat.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class XPManager {
    public static int advancementXP = 16; // XP awarded for advancements
    public static int playerXP = 9; // XP awarded for killing players
    public static int oreXP = 2; // XP awarded for mining ores that are not dropping resources
    public static int animalXP = 3; // XP awarded for killing animals
    public static int monsterXP = 6; // XP awarded for killing monsters

    public static void addXP(Player player, int xpToAdd) {
        player.giveExp(xpToAdd);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        int currentXPPoints = (int) (player.getExpToLevel() * (player.getExp() * 100) / 100);
        player.sendTitle("", HexColor.GREEN + "+" + (xpToAdd) + ChatColor.WHITE + " XP "
                        + ChatColor.DARK_GRAY + "[" + currentXPPoints + "/" + player.getExpToLevel() + "]",
                0, 30, 10);
    }
}
