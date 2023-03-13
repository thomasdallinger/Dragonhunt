package com.tharox.dragonhunt.blocks;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.modifiers.classes.Diamonds;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
                player.sendMessage(Prefixes.getErrorPrefix() + "You are not allowed to break "
                        + HexColor.RED + "Obsidian" + ChatColor.RED + " close to this Nether Portal.");
                return;
            }
        }

        // prevent players from breaking spawners
        if (block.getType() == Material.SPAWNER) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You are not allowed to break Spawners.");
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

            return;
        }

        /**
         * Whenever a player breaks a log or leaves block, there is a chance
         * that its surrounding blocks should also decay. Instead of waiting
         * for the first leave to decay naturally, we give players instant feedback.
         */
        onBlockRemove(event.getBlock(), breakDelay);

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
        materials.put(Material.RAW_COPPER, new ItemStack(Material.COPPER_INGOT, 4));
        materials.put(Material.RAW_GOLD, new ItemStack(Material.GOLD_INGOT, 2));
        materials.put(Material.GOLD_NUGGET, new ItemStack(Material.GOLD_INGOT, 2)); // Nether Gold Ore
        materials.put(Material.GILDED_BLACKSTONE, new ItemStack(Material.GOLD_INGOT, 2));
        materials.put(Material.GRAVEL, new ItemStack(Material.FLINT)); // for Flint and Steel
        materials.put(Material.CLAY, new ItemStack(Material.FLINT)); // for Flint and Steel
        materials.put(Material.MELON_SLICE, new ItemStack(Material.APPLE));
        materials.put(Material.WHEAT, new ItemStack(Material.APPLE));
        materials.put(Material.CARROT, new ItemStack(Material.APPLE));
        materials.put(Material.POTATO, new ItemStack(Material.APPLE));
        materials.put(Material.BEETROOT, new ItemStack(Material.APPLE));
        materials.put(Material.NETHER_WART, new ItemStack(Material.APPLE));
        materials.put(Material.SWEET_BERRIES, new ItemStack(Material.APPLE));
        materials.put(Material.GLOW_BERRIES, new ItemStack(Material.APPLE));

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

            // instead of dropping the following items, give the player a certain amount of XP
            if (item.getType() == Material.COAL
                    || item.getType() == Material.LAPIS_LAZULI
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

    private final long breakDelay = 5; // in ticks
    private final long decayDelay = 2;
    private final List<Block> scheduledBlocks = new ArrayList<>();
    private static final List<BlockFace> NEIGHBORS = Arrays
            .asList(BlockFace.UP,
                    BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
                    BlockFace.DOWN);

    /**
     * Leaves decay has a tendency to cascade. Whenever leaves decay,
     * we want to check its neighbors to find out if they will also
     * decay.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLeavesDecay(LeavesDecayEvent event) {
        onBlockRemove(event.getBlock(), decayDelay);
    }

    /**
     * Check if block is either leaves or a log.
     * If so, schedule their respective removal via
     * {@link #decay(Block) block()}.
     * The latter will perform all necessary checks, including distance.
     *
     * @param oldBlock the block
     * @param delay    the delay of the scheduled check, in ticks
     */
    private void onBlockRemove(final Block oldBlock, long delay) {
        if (!Tag.LOGS.isTagged(oldBlock.getType())
                && !Tag.LEAVES.isTagged(oldBlock.getType())) {
            return;
        }

        Collections.shuffle(NEIGHBORS);
        for (BlockFace neighborFace : NEIGHBORS) {
            final Block block = oldBlock.getRelative(neighborFace);
            if (!Tag.LEAVES.isTagged(block.getType())) continue;
            if (scheduledBlocks.contains(block)) continue;
            Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> decay(block), delay);
            scheduledBlocks.add(block);
        }
    }

    /**
     * Decay if it is a leaves block and its distance to the nearest
     * log block is 4 or greater.
     * <p>
     * This method may only be called by a scheduler if the given
     * block has previously been added to the scheduledBlocks set,
     * from which it will be removed.
     * </p>
     *
     * @param block The block
     * @return true if the block was decayed, false otherwise.
     */
    private boolean decay(Block block) {
        if (!scheduledBlocks.remove(block)) return false;
        if (!block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) return false;
        if (!Tag.LEAVES.isTagged(block.getType())) return false;
        Leaves leaves = (Leaves) block.getBlockData();
        if (leaves.getDistance() < 4) return false;

        LeavesDecayEvent event = new LeavesDecayEvent(block);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        block.getWorld().playSound(block.getLocation(),
                Sound.BLOCK_GRASS_BREAK,
                SoundCategory.BLOCKS, 0.05f, 1.2f);

        block.breakNaturally();

        Random random = new Random();
        int chance = random.nextInt(75);

        if (chance == 0) { // 1,33% chance
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.APPLE));
        }

        return true;
    }
}