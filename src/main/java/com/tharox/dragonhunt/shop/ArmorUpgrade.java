package com.tharox.dragonhunt.shop;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ArmorUpgrade extends EnchantmentUpgrade {
    public static HashMap<Player, Integer> Protection = new HashMap<>();
    public static HashMap<Player, Integer> Thorns = new HashMap<>();
    public static HashMap<Player, Integer> FeatherFalling = new HashMap<>();
    public static HashMap<Player, Integer> DepthStrider = new HashMap<>();
    public static HashMap<Player, Integer> Respiration = new HashMap<>();
    public static HashMap<Player, Integer> AquaAffinity = new HashMap<>();

    public ArmorUpgrade(Player player, String enchantment, int level) {
        this.player = player;
        this.level = level;

        if (enchantment.contains("Protection")) {
            enchantGear(Enchantment.PROTECTION_ENVIRONMENTAL);

            if (Protection.get(player) <= 3) {
                Protection.put(player, Protection.get(player) + 1);
            }
        } else if (enchantment.contains("Thorns")) {
            enchantGear(Enchantment.THORNS);

            if (Thorns.get(player) <= 2) {
                Thorns.put(player, Thorns.get(player) + 1);
            }
        } else if (enchantment.contains("Feather Falling")) {
            enchantGear(Enchantment.PROTECTION_FALL);

            if (FeatherFalling.get(player) <= 3) {
                FeatherFalling.put(player, FeatherFalling.get(player) + 1);
            }
        } else if (enchantment.contains("Depth Strider")) {
            enchantGear(Enchantment.DEPTH_STRIDER);

            if (DepthStrider.get(player) <= 3) {
                DepthStrider.put(player, DepthStrider.get(player) + 1);
            }
        } else if (enchantment.contains("Respiration")) {
            enchantGear(Enchantment.OXYGEN);

            if (Respiration.get(player) <= 3) {
                Respiration.put(player, Respiration.get(player) + 1);
            }
        } else if (enchantment.contains("Aqua Affinity")) {
            enchantGear(Enchantment.WATER_WORKER);

            if (AquaAffinity.get(player) == 1) {
                AquaAffinity.put(player, AquaAffinity.get(player) + 1);
            }
        }
    }

    @Override
    public void enchantGear(Enchantment enchantment) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack item : armor) {
            if (item != null) {
                item.addUnsafeEnchantment(enchantment, level);
            }
        }
    }
}
