package com.tharox.dragonhunt.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Stream;

public class BlockExplodeListener implements Listener {

    /**
     * Chests should not get exploded by entities (Creepers, TNT, ...).
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        Stream.of(Material.CHEST, Material.EMERALD_ORE,
                Material.DEEPSLATE_EMERALD_ORE, Material.LAPIS_ORE,
                Material.DEEPSLATE_LAPIS_ORE, Material.REDSTONE_ORE,
                Material.DEEPSLATE_REDSTONE_ORE).forEach(material -> blocks.removeIf(block -> block.getType() == material));

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
