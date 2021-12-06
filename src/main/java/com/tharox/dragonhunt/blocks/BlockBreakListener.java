package com.tharox.dragonhunt.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        //Deny Block Breaking in the nether
        if (player.getWorld().getName().equals("world_nether")) {
            if (block.getType() != Material.FIRE) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.RED + "You can't break blocks in the nether!");
                return;
            }
        }

        //Destroy Trees
        if (destroyTree(block)) {
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 1, 1);
            event.setCancelled(true);
            return;
        }

        setDamageToUsedTool(player);

        changeDrops(block);

        givePlayerXP(player);
    }

    /**
     * Chests should not get exploded by entities.
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();

        blocks.removeIf(block -> block.getType() == Material.CHEST);
    }

    /**
     * Sets 1 damage to the players used tool to break the block.
     *
     * @param player person who broke the block
     */
    private void setDamageToUsedTool(Player player) {
        ItemStack hand = player.getItemInHand();
        ItemMeta meta = hand.getItemMeta();
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(hand.getDurability() + 1);
        }
        hand.setItemMeta(meta);
    }

    /**
     * Changes the block drops of certain drops to a new ItemStack.
     * If it is not one of the blocks it will just drop the item normally.
     *
     * @param block block destroyed by the player
     */
    private void changeDrops(Block block) {
        HashMap<Material, ItemStack> materials = new HashMap<>();
        materials.put(Material.COAL, new ItemStack(Material.TORCH, 4));
        materials.put(Material.RAW_IRON, new ItemStack(Material.IRON_INGOT));
        materials.put(Material.RAW_GOLD, new ItemStack(Material.GOLD_INGOT));
        materials.put(Material.RAW_COPPER, new ItemStack(Material.EXPERIENCE_BOTTLE, 2));
        materials.put(Material.LAPIS_ORE, new ItemStack(Material.EXPERIENCE_BOTTLE, 2));
        materials.put(Material.GRAVEL, new ItemStack(Material.FLINT));

        for (ItemStack item : block.getDrops()) {

            if (materials.containsKey(item.getType())) {
                block.getDrops().clear();
                block.setType(Material.AIR);

                block.getWorld().dropItemNaturally(block.getLocation(), materials.get(item.getType()));
            }
        }
    }

    private void givePlayerXP(Player player) {
        if (player.getExp() + (float) 0.025 > 1) {
            player.setLevel(player.getLevel() + 1);
            player.setExp(0);
        } else {
            player.setExp(player.getExp() + (float) 0.025);
        }
    }

    private boolean destroyTree(Block block) {
        if (block.getType() != Material.ACACIA_LOG && block.getType() != Material.BIRCH_LOG && block.getType() != Material.DARK_OAK_LOG
                && block.getType() != Material.JUNGLE_LOG && block.getType() != Material.OAK_LOG && block.getType() != Material.SPRUCE_LOG
                && block.getType() != Material.CRIMSON_STEM && block.getType() != Material.WARPED_STEM) return false;

        Location loc = block.getLocation();

        block.getDrops().clear();
        block.setType(Material.AIR);

        block.getWorld().dropItemNaturally(loc, new ItemStack(Material.OAK_LOG));

        ArrayList<Block> locations = new ArrayList<>();

        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++)
                for (int y = 0; y <= 1; y++)
                    locations.add(new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z).getBlock());

        for (Block nextBlock :
                locations) {
            destroyTree(nextBlock);
        }

        return true;
    }
}