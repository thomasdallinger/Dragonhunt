package com.tharox.dragonhunt.shop;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ToolUpgrade extends EnchantmentUpgrade {
    public static HashMap<Player, Integer> Efficiency = new HashMap<>();
    public static HashMap<Player, Integer> Unbreaking = new HashMap<>();

    public ToolUpgrade(Player player, String enchantment, int level) {
        this.player = player;
        this.level = level;

        if (enchantment.contains("Efficiency")) {
            enchantGear(Enchantment.DIG_SPEED);

            if (Efficiency.get(player) <= 3) {
                Efficiency.put(player, Efficiency.get(player) + 1);
            }
        } else if (enchantment.contains("Unbreaking")) {
            enchantGear(Enchantment.DURABILITY);

            if (Unbreaking.get(player) <= 3) {
                Unbreaking.put(player, Unbreaking.get(player) + 1);
            }
        }
    }

    @Override
    public void enchantGear(Enchantment enchantment) {
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (item.getType().equals(Material.WOODEN_PICKAXE) || item.getType().equals(Material.STONE_PICKAXE)
                        || item.getType().equals(Material.GOLDEN_PICKAXE) || item.getType().equals(Material.IRON_PICKAXE)
                        || item.getType().equals(Material.DIAMOND_PICKAXE) || item.getType().equals(Material.NETHERITE_PICKAXE)
                        || item.getType().equals(Material.WOODEN_AXE) || item.getType().equals(Material.STONE_AXE)
                        || item.getType().equals(Material.GOLDEN_AXE) || item.getType().equals(Material.IRON_AXE)
                        || item.getType().equals(Material.DIAMOND_AXE) || item.getType().equals(Material.NETHERITE_AXE)) {
                    item.addEnchantment(enchantment, level);
                }
            }
        }
    }
}
