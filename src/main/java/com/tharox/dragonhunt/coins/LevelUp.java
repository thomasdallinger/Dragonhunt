package com.tharox.dragonhunt.coins;

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
        player.sendTitle(ChatColor.AQUA + " ", HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "LEVEL "
                + level, 5, 60, 5);
        player.sendMessage(HexColor.GREEN + "Level Up!");

        if (level % 5 == 0) {
            player.sendMessage(HexColor.HIGHLIGHT + "⛃" + ChatColor.DARK_GRAY + " | "
                    + HexColor.GREEN + "+1" + ChatColor.GRAY + " Golden Apple");
            GameState.putItemInInventory(player, new ItemStack(Material.GOLDEN_APPLE));
            Coins.addCoins(player, 20); // extra coins
        } else {
            Coins.addCoins(player, 10);
        }
    }
}
