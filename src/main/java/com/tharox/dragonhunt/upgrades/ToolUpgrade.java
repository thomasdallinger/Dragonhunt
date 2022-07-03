package com.tharox.dragonhunt.upgrades;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class ToolUpgrade extends EnchantmentUpgrade {
    public static HashMap<Player, Integer> Efficiency = new HashMap<>();
    public static HashMap<Player, Integer> Unbreakable = new HashMap<>();

    // a list of all tools affected by the Tool Upgrades
    protected static List<Material> tools = List.of(
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE,
            Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.WOODEN_AXE, Material.STONE_AXE,
            Material.GOLDEN_AXE, Material.IRON_AXE,
            Material.DIAMOND_AXE, Material.NETHERITE_AXE);

    public ToolUpgrade(Player player, String enchantment, int level) {
        this.player = player;
        this.level = level;

        if (enchantment.contains("Efficiency")) {
            enchantGear(Enchantment.DIG_SPEED);

            if (Efficiency.get(player) <= 3) {
                Efficiency.put(player, Efficiency.get(player) + 1);
            }
        } else if (enchantment.contains("Unbreakable")) {
            makeToolsUnbreakable();

            if (Unbreakable.get(player) == 1) {
                Unbreakable.put(player, Unbreakable.get(player) + 1);
            }
        }
    }

    @Override
    public void enchantGear(Enchantment enchantment) {
        for (ItemStack item : player.getInventory()) {
            if (item != null && tools.contains(item.getType())) {
                item.addEnchantment(enchantment, level);
            }
        }
    }

    public void makeToolsUnbreakable() {
        for (ItemStack item : player.getInventory()) {
            if (item != null && tools.contains(item.getType())) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.DURABILITY, 1, true); // enchanted look
                    meta.setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                item.setItemMeta(meta);
            }
        }
    }
}
