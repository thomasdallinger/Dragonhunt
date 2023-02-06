package com.tharox.dragonhunt.movement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class TridentSwapRunnable extends BukkitRunnable {
    public static final HashMap<Player, ItemStack> playerMainHandItem = new HashMap<>();
    public static final HashMap<Player, Integer> playerMainHandSlot = new HashMap<>();
    public static final HashMap<Player, ItemStack> playerOffHandItem = new HashMap<>();
    private final HashMap<Player, Boolean> playerWasInWater = new HashMap<>();

    public TridentSwapRunnable() {
        Bukkit.getOnlinePlayers().forEach(player -> playerWasInWater.put(player, false));
    }

    /**
     * Checks if the player is at least 2 or 6 blocks above the ground.
     *
     * @param player player to be checked if in the air
     * @return true if the player is at least 2 blocks above the ground and was in the air,<br>
     * true if the player is at least 6 blocks above the ground and was not in the air
     */
    private boolean isPlayerNowInWater(Player player) {
        Material material = player.getLocation().getBlock().getType();
        return material == Material.WATER;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // player is no longer in water - give the mainhand and offhand item back (if it wasn't air)
            if (playerWasInWater.get(player) && !isPlayerNowInWater(player)) {
                playerWasInWater.put(player, false);
                player.getInventory().setItem(playerMainHandSlot.get(player), playerMainHandItem.get(player));
                player.getInventory().setItemInOffHand(playerOffHandItem.get(player));
            }

            // player is currently in water - replace the mainhand item with a trident,
            // the offhand with AIR (to avoid a visual bug)
            else if (!playerWasInWater.get(player) && isPlayerNowInWater(player)) {
                playerWasInWater.put(player, true);
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();

                if (mainHand.getType() == Material.TRIDENT) {
                    playerMainHandItem.put(player, new ItemStack(Material.AIR));
                    playerOffHandItem.put(player, new ItemStack(Material.AIR));
                } else {
                    playerMainHandItem.put(player, mainHand);
                    playerMainHandSlot.put(player, player.getInventory().getHeldItemSlot());
                    playerOffHandItem.put(player, offHand);
                }

                ItemStack trident = new ItemStack(Material.TRIDENT);
                trident.addEnchantment(Enchantment.RIPTIDE, 3);
                player.getInventory().setItemInMainHand(trident);
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        }
    }
}
