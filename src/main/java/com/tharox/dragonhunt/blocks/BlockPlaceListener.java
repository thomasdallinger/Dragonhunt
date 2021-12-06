package com.tharox.dragonhunt.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(player.getWorld().getName().equals("world_nether")) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(ChatColor.RED + "You can't place blocks in the nether!");
            return;
        }

        if(block.getType() == Material.BREWING_STAND || block.getType() == Material.ENCHANTING_TABLE
                || block.getType() == Material.ENDER_CHEST || block.getType() == Material.CHEST
                || block.getType() == Material.CHEST_MINECART) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(ChatColor.RED + "You are not allowed to place this block!");
        }
    }
}
