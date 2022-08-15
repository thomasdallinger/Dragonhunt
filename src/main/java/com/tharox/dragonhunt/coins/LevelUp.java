package com.tharox.dragonhunt.coins;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class LevelUp implements Listener {

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        int level = event.getNewLevel();

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.sendMessage(Prefixes.getCoinPrefix() + HexColor.HIGHLIGHT + "Level "
                + ChatColor.DARK_PURPLE + level + HexColor.HIGHLIGHT + " reached!");

        if (level % 5 == 0) {
            player.sendMessage(Prefixes.getCoinPrefix() + HexColor.GREEN + "+1" + ChatColor.GRAY + " Golden Apple");
            GameState.putItemInInventory(player, new ItemStack(Material.GOLDEN_APPLE));
            Coins.addCoins(player, 20); // extra coins
        } else {
            Coins.addCoins(player, 10);
        }
    }
}
