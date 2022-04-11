package com.tharox.dragonhunt.blocks;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.points.GearPoints;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockBreakListener implements Listener {

    protected static List<Material> woods = new ArrayList<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        List<Material> netherOres = new ArrayList<>();
        final int[] blocksDestroyed = {10};

        netherOres.add(Material.DEEPSLATE_LAPIS_ORE);
        netherOres.add(Material.DEEPSLATE_IRON_ORE);
        netherOres.add(Material.DEEPSLATE_GOLD_ORE);

        //Deny Block Breaking in the nether
        if (player.getWorld().getName().equals("world_nether")) {
            if (block.getType() != Material.FIRE && !netherOres.contains(block.getType())) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.RED + "You can't break blocks in the nether!");
                return;
            }
        }

        //Destroy Trees
        if (destroyTree(block, player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 1, 1);
                    blocksDestroyed[0]--;
                    if(blocksDestroyed[0] == 0) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 2);

            event.setCancelled(true);
            return;
        }

        setDamageToUsedTool(player);
        changeDrops(block);
        givePlayerXP(block, player);
    }

    /**
     * Sets 1 damage to the players used tool to break the block.
     *
     * @param player person who broke the block
     */
    private void setDamageToUsedTool(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemMeta meta = hand.getItemMeta();
        if (meta instanceof Damageable) {
            if(hand.getType().getMaxDurability() > 0) {
                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + 1);

                //avoid negative durability
                if(((Damageable) meta).getDamage() > hand.getType().getMaxDurability()) {
                    player.getInventory().removeItem(hand);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    return;
                }
            }
        }
        hand.setItemMeta(meta);
    }

    /**
     * Changes the block drops of certain drops to a new ItemStack.
     * If it is not one of the blocks it will just drop the item normally.
     *
     * @param block block destroyed by the player
     */
    protected static void changeDrops(Block block) {
        HashMap<Material, ItemStack> materials = new HashMap<>();
        materials.put(Material.COAL, new ItemStack(Material.TORCH, 4));
        materials.put(Material.RAW_IRON, new ItemStack(Material.IRON_INGOT, 2));
        materials.put(Material.RAW_GOLD, new ItemStack(Material.GOLD_INGOT, 2));
        materials.put(Material.GRAVEL, new ItemStack(Material.FLINT));

        for (ItemStack item : block.getDrops()) {
            if (materials.containsKey(item.getType())) {
                block.getDrops().clear();
                block.setType(Material.AIR);

                block.getWorld().dropItemNaturally(block.getLocation(), materials.get(item.getType()));
            }
        }
    }

    protected void givePlayerXP(Block block, Player player) {
        for (ItemStack item : block.getDrops()) {
            if (item.getType() == Material.LAPIS_LAZULI
                    || item.getType() == Material.EMERALD
                    || item.getType() == Material.REDSTONE) {
                block.getDrops().clear();
                block.setType(Material.AIR);

                player.giveExp(GearPoints.oreXP);
            }
        }
    }

    private boolean destroyTree(Block block, Player player) {
        woods.add(Material.ACACIA_LOG);
        woods.add(Material.BIRCH_LOG);
        woods.add(Material.DARK_OAK_LOG);
        woods.add(Material.JUNGLE_LOG);
        woods.add(Material.OAK_LOG);
        woods.add(Material.SPRUCE_LOG);
        woods.add(Material.CRIMSON_STEM);
        woods.add(Material.WARPED_STEM);

        if(!woods.contains(block.getType())) return false;

        Location loc = block.getLocation();
        block.getDrops().clear();
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(loc, new ItemStack(Material.OAK_LOG));

        setDamageToUsedTool(player);

        ArrayList<Block> locations = new ArrayList<>();

        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++)
                for (int y = 0; y <= 1; y++)
                    locations.add(new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z).getBlock());

        for (Block nextBlock :
                locations) {
            destroyTree(nextBlock, player);
        }

        return true;
    }
}