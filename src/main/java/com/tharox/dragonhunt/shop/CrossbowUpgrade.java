package com.tharox.dragonhunt.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CrossbowUpgrade extends EnchantmentUpgrade {
    public static HashMap<Player, Integer> Piercing = new HashMap<>();
    public static HashMap<Player, Integer> Multishot = new HashMap<>();
    public static HashMap<Player, Integer> QuickCharge = new HashMap<>();

    public CrossbowUpgrade(Player player, String enchantment, int level) {
        this.player = player;
        this.level = level;

        if (enchantment.contains("Piercing")) {
            enchantGear(Enchantment.PIERCING);

            if (Piercing.get(player) <= 3) {
                Piercing.put(player, Piercing.get(player) + 1);
            }
        } else if (enchantment.contains("Multishot")) {
            enchantGear(Enchantment.MULTISHOT);

            if (Multishot.get(player) == 1) {
                Multishot.put(player, Multishot.get(player) + 1);
            }
        } else if (enchantment.contains("Quick Charge")) {
            enchantGear(Enchantment.QUICK_CHARGE);

            if (QuickCharge.get(player) <= 2) {
                QuickCharge.put(player, QuickCharge.get(player) + 1);
            }
        }
    }

    @Override
    public void enchantGear(Enchantment enchantment) {
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (item.getType().equals(Material.CROSSBOW)) {
                    if(item.getItemMeta() != null
                            && item.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Grappler")
                            && enchantment.equals(Enchantment.MULTISHOT)) return;
                    item.addEnchantment(enchantment, level);
                }
            }
        }
    }
}
