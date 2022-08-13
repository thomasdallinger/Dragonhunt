package com.tharox.dragonhunt.coins;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.ChatColor;
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
        player.sendTitle(ChatColor.AQUA + " ", ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "LEVEL "
                + level, 5, 60, 5);
        player.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "LEVEL UP REWARDS:");

        if (level % 5 == 0) {
            player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "+1"
                    + ChatColor.GOLD + " Golden Apple");
            GameState.putItemInInventory(player, new ItemStack(Material.GOLDEN_APPLE));
            Coins.addCoins(player, 2); // extra coin
        } else {
            Coins.addCoins(player, 1);
        }
    }
}
