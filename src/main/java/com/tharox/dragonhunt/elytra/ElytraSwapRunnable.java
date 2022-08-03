package com.tharox.dragonhunt.elytra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ElytraSwapRunnable extends BukkitRunnable {
    private final HashMap<Player, ItemStack> playerChestplate = new HashMap<>();
    private final HashMap<Player, Boolean> playerWasInAir = new HashMap<>();

    /**
     * Checks if the player is at least 5 blocks above the ground.
     *
     * @param p player to be checked if in the air
     * @return true if the player is 5 blocks above the ground otherwise false
     */
    private boolean isPlayerNowInTheAir(Player p) {
        for (int blocksBelow = 1; blocksBelow <= 6; blocksBelow++) {
            if (p.getLocation().subtract(0, blocksBelow, 0).getBlock().getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    private ItemStack getUnbreakableElytra() {
        ItemStack item = new ItemStack(Material.ELYTRA);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setUnbreakable(true);
        meta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Elytra");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public ElytraSwapRunnable() {
        Bukkit.getOnlinePlayers().forEach(player -> playerWasInAir.put(player, false));
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // player is no longer in the air - give the chestplate back (if it wasn't air)
            if (playerWasInAir.get(player) && !isPlayerNowInTheAir(player)) {
                playerWasInAir.put(player, false);
                player.getInventory().setChestplate(playerChestplate.get(player));

                // player is currently in the air - replace the chestplate with an elytra
            } else if (!playerWasInAir.get(player) && isPlayerNowInTheAir(player)) {
                playerWasInAir.put(player, true);
                ItemStack chestplate = player.getInventory().getChestplate();

                if (chestplate != null && chestplate.getType() == Material.ELYTRA)
                    playerChestplate.put(player, new ItemStack(Material.AIR));
                else
                    playerChestplate.put(player, chestplate);

                player.getInventory().setChestplate(getUnbreakableElytra());
            }

            // make the player glow while in the air
            if (isPlayerNowInTheAir(player)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0));
            }
        }
    }
}
