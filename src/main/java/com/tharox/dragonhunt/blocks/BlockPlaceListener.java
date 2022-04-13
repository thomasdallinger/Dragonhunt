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

        // prevent players from placing blocks in the NETHER
        // EYES OF ENDER can be placed into END PORTAL FRAMES to open up the END PORTAL
        if (player.getWorld().getName().equals("world_nether")) {
            if (block.getType() != Material.END_PORTAL_FRAME) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.RED + "You can't place blocks in the nether!");
                return;
            }
        }

        // prevent players from placing beds in the END
        // this only considers WHITE BEDS, since all sheeps drop strings which can be crafted to WHITE WOOL
        if (player.getWorld().getName().equals("world_the_end")) {
            if (block.getType() == Material.WHITE_BED) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.RED + "You can't place beds in the end!");
                return;
            }
        }

        // prevent players from placing certain blocks in ANY dimension
        if (block.getType() == Material.BREWING_STAND || block.getType() == Material.ENCHANTING_TABLE
                || block.getType() == Material.ENDER_CHEST || block.getType() == Material.CHEST
                || block.getType() == Material.CHEST_MINECART || block.getType() == Material.ANVIL) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(ChatColor.RED + "You are not allowed to place this block!");
        }
    }
}
