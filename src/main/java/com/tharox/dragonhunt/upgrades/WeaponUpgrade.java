package com.tharox.dragonhunt.upgrades;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class WeaponUpgrade extends EnchantmentUpgrade {
    public static HashMap<Player, Integer> Sharpness = new HashMap<>();
    public static HashMap<Player, Integer> Knockback = new HashMap<>();
    public static HashMap<Player, Integer> FireAspect = new HashMap<>();

    // a list of all weapons affected by the Weapon Upgrades
    protected static List<Material> weapons = List.of(
            Material.IRON_SWORD, Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD, Material.IRON_AXE,
            Material.DIAMOND_AXE, Material.NETHERITE_AXE);

    public WeaponUpgrade(Player player, String enchantment, int level) {
        this.player = player;
        this.level = level;

        if (enchantment.contains("Sharpness")) {
            enchantGear(Enchantment.DAMAGE_ALL);

            if (Sharpness.get(player) <= 3) {
                Sharpness.put(player, Sharpness.get(player) + 1);
            }
        } else if (enchantment.contains("Knockback")) {
            enchantGear(Enchantment.KNOCKBACK);

            if (Knockback.get(player) <= 2) {
                Knockback.put(player, Knockback.get(player) + 1);
            }
        } else if (enchantment.contains("Fire Aspect")) {
            enchantGear(Enchantment.FIRE_ASPECT);

            if (FireAspect.get(player) <= 2) {
                FireAspect.put(player, FireAspect.get(player) + 1);
            }
        }
    }

    @Override
    public void enchantGear(Enchantment enchantment) {
        for (ItemStack item : player.getInventory()) {
            if (item != null && weapons.contains(item.getType())) {
                item.addUnsafeEnchantment(enchantment, level);
            }
        }
    }
}
