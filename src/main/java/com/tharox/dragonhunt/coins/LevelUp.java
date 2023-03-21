package com.tharox.dragonhunt.coins;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.chat.HexColor;
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
        // 20 coins for reaching level 0 once a second player joins.
        if (level == 0) return;

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        int coinsToAdd;

        if (level % 5 == 0) {
            coinsToAdd = 20;
        } else {
            coinsToAdd = 10;
        }

        Coins.addCoins(player, coinsToAdd);
        player.sendMessage(Prefixes.getCoinPrefix(coinsToAdd) + HexColor.HIGHLIGHT + ChatColor.BOLD + "Level "
                + ChatColor.WHITE + ChatColor.BOLD + level + HexColor.HIGHLIGHT + ChatColor.BOLD + " reached.");
    }
}
