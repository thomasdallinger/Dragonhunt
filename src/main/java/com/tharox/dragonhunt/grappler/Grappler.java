package com.tharox.dragonhunt.grappler;

import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import java.util.Arrays;
import java.util.HashMap;

public class Grappler {
    public static HashMap<Player, Integer> grapplerCooldown = new HashMap<>();

    public Grappler(Player player) {
        player.getInventory().setItem(8, createGrappler());
    }

    private ItemStack createGrappler() {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
        if (meta == null) return null;

        meta.addChargedProjectile(new ItemStack(Material.ARROW, 1));
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Grappler");
        meta.setLore(Arrays.asList(
                ChatColor.BLUE.toString() + ChatColor.ITALIC + "Pull yourself to different locations.",
                ChatColor.BLUE.toString() + ChatColor.ITALIC + "Arrows shot can't do damage,",
                ChatColor.BLUE.toString() + ChatColor.ITALIC + "but can be picked up.",
                ChatColor.BLUE.toString() + ChatColor.ITALIC + "Be aware of fall damage.",
                "",
                ChatColor.WHITE + "Max. arrow distance: " + HexColor.RED + "20 blocks"));
        item.setItemMeta(meta);

        return item;
    }
}
