package com.tharox.dragonhunt.blocks;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.modifiers.classes.Diamonds;
import com.tharox.dragonhunt.upgrades.XPUpgrade;
import net.md_5.bungee.api.ChatColor;
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

    // a list of all wood types currently in the game + cactus
    public static List<Material> woods = List.of(
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG, Material.OAK_LOG, Material.SPRUCE_LOG,
            Material.MANGROVE_WOOD, Material.CRIMSON_STEM, Material.WARPED_STEM,
            Material.CACTUS);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // prevent players from breaking obsidian close to the NETHER portal inside the NETHER
        if (GameState.firstNetherEntry != null && player.getWorld().getName().equals("world_nether")) {
            if ((int) player.getLocation().distance(GameState.firstNetherEntry) < 15 &&
                    block.getType() == Material.OBSIDIAN) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You are not allowed to break "
                        + HexColor.RED + "Obsidian" + ChatColor.GRAY + " close to this Nether Portal.");
                return;
            }
        }

        // prevent players from breaking spawners
        if (block.getType() == Material.SPAWNER) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You are not allowed to break Spawners.");
            return;
        }

        // on top of dropping amethysts, give the player XP
        if (block.getType() == Material.AMETHYST_CLUSTER) {
            Coins.addXP(player, Coins.oreXP);
        }

        // play the break wood sound for the amount of blocks destroyed
        final int[] blocksDestroyed = {10};

        // destroy trees
        if (destroyTree(block, player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 1, 1);
                    blocksDestroyed[0]--;
                    if (blocksDestroyed[0] == 0) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 2);

            event.setCancelled(true);

            if (GameState.treesDestroyed.containsKey(player))
                GameState.treesDestroyed.put(player, GameState.treesDestroyed.get(player) + 1);
            else
                GameState.treesDestroyed.put(player, 1); // first tree destroyed

            if (GameState.treesDestroyed.get(player) % 6 == 0) // every sixth tree drops an apple
                GameState.putItemInInventory(player, new ItemStack(Material.APPLE));

            if (GameState.treesDestroyed.get(player) % 12 == 0) // every twelfth tree drops a golden apple
                GameState.putItemInInventory(player, new ItemStack(Material.GOLDEN_APPLE));

            return;
        }

        changeDrops(block, player);
    }

    /**
     * Changes the drops of certain blocks to a different ItemStack.
     * If the block has drops, which are not affected by any changes, they will drop unchanged.
     *
     * @param block  block destroyed by the player
     * @param player player that destroyed the specific block
     */
    protected void changeDrops(Block block, Player player) {
        // a HashMap with the old drops as materials and new drops as ItemStacks
        HashMap<Material, ItemStack> materials = new HashMap<>();
        materials.put(Material.RAW_IRON, new ItemStack(Material.IRON_INGOT, 2));
        materials.put(Material.RAW_COPPER, new ItemStack(Material.COPPER_INGOT, 2));
        materials.put(Material.RAW_GOLD, new ItemStack(Material.GOLD_INGOT, 2));
        materials.put(Material.GOLD_NUGGET, new ItemStack(Material.GOLD_INGOT, 1)); // Nether Gold Ore
        materials.put(Material.GILDED_BLACKSTONE, new ItemStack(Material.GOLD_INGOT, 1));
        materials.put(Material.COAL, new ItemStack(Material.TORCH, 4));
        materials.put(Material.GRAVEL, new ItemStack(Material.FLINT)); // for Flint and Steel
        materials.put(Material.MELON_SLICE, new ItemStack(Material.GOLDEN_CARROT));
        materials.put(Material.WHEAT, new ItemStack(Material.GOLDEN_CARROT));
        materials.put(Material.CARROT, new ItemStack(Material.GOLDEN_CARROT));
        materials.put(Material.POTATO, new ItemStack(Material.GOLDEN_CARROT));
        materials.put(Material.BEETROOT, new ItemStack(Material.GOLDEN_CARROT));
        materials.put(Material.NETHER_WART, new ItemStack(Material.GOLDEN_CARROT));
        materials.put(Material.SWEET_BERRIES, new ItemStack(Material.GOLDEN_CARROT));
        materials.put(Material.GLOW_BERRIES, new ItemStack(Material.GOLDEN_CARROT));

        if (Diamonds.active) {
            materials.put(Material.DIAMOND, new ItemStack(Material.DIAMOND_BLOCK));
        } else {
            materials.put(Material.DIAMOND, new ItemStack(Material.DIAMOND, 2));
        }

        // loop through all drops and check if they have to be changed
        for (ItemStack item : block.getDrops()) {
            if (materials.containsKey(item.getType())) {
                block.getDrops().clear();
                block.setType(Material.AIR);

                block.getWorld().dropItemNaturally(block.getLocation(), materials.get(item.getType()));
            }

            // instead of dropping LAPIS, EMERALDS, REDSTONE or QUARTZ give the player a certain amount of XP
            if (item.getType() == Material.LAPIS_LAZULI
                    || item.getType() == Material.EMERALD
                    || item.getType() == Material.REDSTONE
                    || item.getType() == Material.QUARTZ) {
                block.getDrops().clear();
                block.setType(Material.AIR);

                Coins.addXP(player, Coins.oreXP);
            }
        }
    }

    /**
     * When mining one block of wood, this function tries to find nearby wood block.
     * If it finds one, this process starts from the beginning.
     *
     * @param block  block that has been destroyed by the player
     * @param player player who destroyed the block
     * @return true if it was successful, false if the destroyed block isn't a wood type
     */
    private boolean destroyTree(Block block, Player player) {
        if (!woods.contains(block.getType())) return false;

        block.getDrops().clear();
        block.setType(Material.AIR);
        GameState.putItemInInventory(player, new ItemStack(Material.OAK_LOG));

        Location location = block.getLocation();
        ArrayList<Block> locations = new ArrayList<>();

        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++)
                for (int y = -1; y <= 1; y++)
                    locations.add(new Location(location.getWorld(), location.getX() + x, location.getY() + y,
                            location.getZ() + z).getBlock());

        for (Block nextBlock :
                locations) {
            destroyTree(nextBlock, player);
        }

        return true;
    }
}