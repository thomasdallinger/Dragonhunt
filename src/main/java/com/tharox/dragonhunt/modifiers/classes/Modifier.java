package com.tharox.dragonhunt.modifiers.classes;

import com.tharox.dragonhunt.chat.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public abstract class Modifier {
    public abstract void setActive(boolean isActive);

    public abstract boolean isPositive();

    public abstract String getTitle();

    /**
     * @return an item representing the modifier in the inventory shown to all players at the start of the game
     */
    public static ItemStack getDisplayItem(String title, String lore, Material material, boolean positive,
                                           boolean active) {
        String titleColor;

        ItemStack item = new ItemStack(material);

        if (active) {
            // enchanted look
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

            if (positive) {
                titleColor = HexColor.GREEN.toString() + ChatColor.BOLD + "▲ "
                        + ChatColor.DARK_GREEN + ChatColor.BOLD;
            } else {
                titleColor = HexColor.RED.toString() + ChatColor.BOLD + "▼ "
                        + ChatColor.DARK_RED + ChatColor.BOLD;
            }
        } else {
            if (positive) {
                titleColor = ChatColor.GRAY.toString() + ChatColor.BOLD + "▲ "
                        + ChatColor.DARK_GRAY + ChatColor.BOLD;
            } else {
                titleColor = ChatColor.GRAY.toString() + ChatColor.BOLD + "▼ "
                        + ChatColor.DARK_GRAY + ChatColor.BOLD;
            }

            // overwrite the modifiers original material when it is not active
            item.setType(Material.BLACK_STAINED_GLASS_PANE);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(titleColor + title);

        // Set the modifier lore.
        meta.setLore(Collections.singletonList(ChatColor.GRAY + lore));
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
            color = HexColor.GREEN.toString() + ChatColor.BOLD + "▲ " + ChatColor.DARK_GREEN;
        } else {
            color = HexColor.RED.toString() + ChatColor.BOLD + "▼ " + ChatColor.DARK_RED;
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
