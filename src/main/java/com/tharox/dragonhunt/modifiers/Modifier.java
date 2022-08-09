package com.tharox.dragonhunt.modifiers;

import com.tharox.dragonhunt.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public abstract class Modifier {
    public abstract void setActive(boolean active);

    public abstract boolean isPositive();

    /**
     * @return an item representing the modifier in the inventory shown to all players at the start of the game
     */
    public static ItemStack getDisplayItem(String title, String lore, boolean positive, Integer impact,
                                           boolean active) {
        Material material;
        String titleColor, loreColor;

        if (positive) { // positive
            material = Material.GREEN_CONCRETE;
            titleColor = ChatColor.DARK_GREEN.toString() + ChatColor.BOLD;
            loreColor = ChatColor.GREEN.toString() + ChatColor.ITALIC;
        } else { // negative
            material = Material.RED_CONCRETE;
            titleColor = ChatColor.DARK_RED.toString() + ChatColor.BOLD;
            loreColor = ChatColor.RED.toString() + ChatColor.ITALIC;
        }

        ItemStack item = new ItemStack(material, impact);

        if (active)
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(titleColor + title);
        meta.setLore(Collections.singletonList(loreColor + lore));

        item.setItemMeta(meta);

        return item;
    }

    /**
     * @return a formatted modifier title message
     */
    public static String displayModifierSelection(String title, boolean positive, boolean active) {
        String color;

        if (!active) return null;

        if (positive) {
            color = ChatColor.GREEN.toString();
        } else {
            color = ChatColor.RED.toString();
        }

        return color + title;
    }

    /**
     * @return a formatted modifier lore message
     */
    public static String getLore(String lore, boolean positive, boolean active) {
        String color;

        if (!active) return null;

        if (positive) {
            color = ChatColor.GREEN.toString();
        } else {
            color = ChatColor.RED.toString();
        }

        return color + lore;
    }
}
