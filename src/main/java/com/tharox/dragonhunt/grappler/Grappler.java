package com.tharox.dragonhunt.grappler;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Grappler {

    public Grappler(Player player) {
        player.getInventory().setItem(8, createGrappler());
    }

    private ItemStack createGrappler() {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addEnchant(Enchantment.QUICK_CHARGE, 2, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Grappler");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "Pull yourself to different locations.",
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "Arrows shot can't do damage,",
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "but can be picked up.",
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "Be aware of fall damage.",
                "",
                ChatColor.WHITE + "Max. arrow distance: " + ChatColor.RED + "20 blocks"));
        item.setItemMeta(meta);

        return item;
    }
}
