package com.tharox.dragonhunt.grappler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Grappler implements Listener {
    public Grappler(Player player) {
        player.getInventory().setItem(8, createGrappler());
    }

    private ItemStack createGrappler() {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Grappler");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "Pull yourself to different locations.",
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "Arrows shot can do damage",
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "and can be picked up.",
                ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "Be aware of fall damage.",
                "",
                ChatColor.WHITE + "Maximum arrow distance: " + ChatColor.RED +  "20 blocks",
                ChatColor.YELLOW + "Buy CROSSBOW UPGRADES to enchant."));
        item.setItemMeta(meta);

        return item;
    }
}
