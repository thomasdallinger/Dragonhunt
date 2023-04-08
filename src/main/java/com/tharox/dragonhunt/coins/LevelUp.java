package com.tharox.dragonhunt.coins;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.kits.classes.KitClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class LevelUp implements Listener {

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        int level = event.getNewLevel();

        // Following check needed to avoid an issue where the first player that joined on the server gets
        // 10 coins for reaching level 0 once a second player joins.
        if (level == 0) return;

        int coinsToAdd = 10;

        // Assassins Passive
        if (KitClass.assassins.contains(player)) {
            coinsToAdd += 2;
        }

        Coins.addCoins(player, coinsToAdd);

        player.sendMessage(Prefixes.getCoinPrefix(coinsToAdd) + HexColor.GREEN + ChatColor.BOLD + "Level "
                + ChatColor.DARK_GREEN + ChatColor.BOLD + level + HexColor.GREEN + ChatColor.BOLD + " reached.");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }
}
