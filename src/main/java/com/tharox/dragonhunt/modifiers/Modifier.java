package com.tharox.dragonhunt.modifiers;

import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
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
    public static ItemStack getDisplayItem(String title, String lore, Material material, boolean positive,
                                           boolean active) {
        String titleColor, loreColor;

        if (positive) { // positive
            titleColor = ChatColor.DARK_GREEN.toString() + ChatColor.BOLD;
            loreColor = HexColor.GREEN.toString() + ChatColor.ITALIC;
        } else { // negative
            titleColor = ChatColor.DARK_RED.toString() + ChatColor.BOLD;
            loreColor = HexColor.RED.toString() + ChatColor.ITALIC;
        }

        ItemStack item = new ItemStack(material);

        if (active)
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(titleColor + title);
        meta.setLore(Collections.singletonList(loreColor + lore));

        item.setItemMeta(meta);

        return item;
    }

    /**
     * @return a formatted modifier title/lore message
     */
    public static String displayModifierSelection(String title, boolean positive, boolean active) {
        String color;

        if (!active) return null;

        if (positive) {
            color = HexColor.GREEN.toString() + ChatColor.BOLD;
        } else {
            color = HexColor.RED.toString() + ChatColor.BOLD;
        }

        return color + title;
    }

    /**
     * @return a modifier lore message
     */
    public static String getLore(String lore, boolean active) {
        if (!active) return null;

        return lore;
    }
}
