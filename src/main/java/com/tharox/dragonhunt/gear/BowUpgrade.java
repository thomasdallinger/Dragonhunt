package com.tharox.dragonhunt.gear;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BowUpgrade extends EnchantmentUpgrade {
    public static HashMap<Player, Integer> Power = new HashMap<>();
    public static HashMap<Player, Integer> Punch = new HashMap<>();
    public static HashMap<Player, Integer> Flame = new HashMap<>();

    public BowUpgrade(Player player, String enchantment, int level) {
        this.player = player;
        this.level = level;

        if (enchantment.contains("Power")) {
            enchantGear(Enchantment.ARROW_DAMAGE);

            if (Power.get(player) <= 3) {
                Power.put(player, Power.get(player) + 1);
            }
        } else if (enchantment.contains("Punch")) {
            enchantGear(Enchantment.ARROW_KNOCKBACK);

            if (Punch.get(player) <= 2) {
                Punch.put(player, Punch.get(player) + 1);
            }
        } else if (enchantment.contains("Flame")) {
            enchantGear(Enchantment.ARROW_FIRE);

            if (Flame.get(player) == 1) {
                Flame.put(player, Flame.get(player) + 1);
            }
        }
    }

    @Override
    public void enchantGear(Enchantment enchantment) {
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (item.getType().equals(Material.BOW)) {
                    item.addEnchantment(enchantment, level);
                }
            }
        }
    }
}
