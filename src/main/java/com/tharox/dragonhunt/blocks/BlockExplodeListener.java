package com.tharox.dragonhunt.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockExplodeListener implements Listener {

    /**
     * Chests should not get exploded by entities (Creepers, TNT, ...).
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();

        blocks.removeIf(block -> block.getType() == Material.CHEST);
        blocks.removeIf(block -> block.getType() == Material.REDSTONE_ORE);
        blocks.removeIf(block -> block.getType() == Material.EMERALD_ORE);
        blocks.removeIf(block -> block.getType() == Material.LAPIS_ORE);

        for (Block block : blocks) {
            if (BlockBreakListener.woods.contains(block.getType())) {
                block.getDrops().clear();
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.OAK_LOG));
                continue;
            }
            BlockBreakListener.changeDrops(block);
        }
    }
}
