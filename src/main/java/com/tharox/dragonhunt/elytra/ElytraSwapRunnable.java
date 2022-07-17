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
    private final HashMap<Player, ItemStack> playerChestPlate = new HashMap<>();
    private final HashMap<Player, Boolean> playerWasInAir = new HashMap<>();

    private boolean isPlayerNowInTheAir(Player p) {
        return p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR
                && p.getLocation().subtract(0, 2, 0).getBlock().getType() == Material.AIR
                && p.getLocation().subtract(0, 3, 0).getBlock().getType() == Material.AIR;
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
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (playerWasInAir.get(player) && !isPlayerNowInTheAir(player)) {
                playerWasInAir.put(player, false);
                player.getInventory().setChestplate(playerChestPlate.get(player));
            } else if (!playerWasInAir.get(player) && isPlayerNowInTheAir(player)) {
                playerWasInAir.put(player, true);
                ItemStack chestPlate = player.getInventory().getChestplate();
                if(chestPlate.getType() == Material.ELYTRA)
                    playerChestPlate.put(player, new ItemStack(Material.AIR));
                else
                    playerChestPlate.put(player, chestPlate);
                player.getInventory().setChestplate(getUnbreakableElytra());
            }

            if (isPlayerNowInTheAir(player)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0));
            }
        });
    }
}
