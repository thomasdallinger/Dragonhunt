package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopInventory {
    public Inventory menu, enchantments, permanentEffects, arrowEffects, kitUpgrades;
    public static final HashMap<String, Integer> itemCost = new HashMap<>();

    public ShopInventory(FileConfiguration config, Player player) {
        menu = createInventory(config, "shop", player);
        enchantments = createInventory(config, "enchanting", player);
        permanentEffects = createInventory(config, "permanentEffects", player);
        arrowEffects = createInventory(config, "negativeEffects", player);
        kitUpgrades = createInventory(config, GameState.selectedKit.get(player).substring(16), player);
    }

    private Inventory createInventory(FileConfiguration config, String category, Player player) {
        ConfigurationSection itemsSection = config.getConfigurationSection(category);
        List<ShopItem> itemList = new ArrayList<>();

        if (itemsSection == null) {
            Bukkit.getLogger().severe("Setup config.yml");
            return null;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);

            itemList.add(new ShopItem(section));
        }

        Inventory inv;
        if (category.contains("enchanting")) {
            inv = Bukkit.createInventory(null, 45,
                    HexColor.PURPLE.toString() + ChatColor.BOLD + "SHOP ENCHANTMENTS "
                            + ChatColor.DARK_GRAY + Coins.getCoins(player) + " ⛃");
            addBackToMenuItem(inv);
        } else if (category.contains("permanentEffects")) {
            inv = Bukkit.createInventory(null, 45,
                    HexColor.PURPLE.toString() + ChatColor.BOLD + "SHOP EFFECTS "
                            + ChatColor.DARK_GRAY + Coins.getCoins(player) + " ⛃");
            addBackToMenuItem(inv);
        } else if (category.contains("negativeEffects")) {
            inv = Bukkit.createInventory(null, 45,
                    HexColor.PURPLE.toString() + ChatColor.BOLD + "SHOP ARROWS "
                            + ChatColor.DARK_GRAY + Coins.getCoins(player) + " ⛃");
            addBackToMenuItem(inv);
        } else if (category.contains(GameState.selectedKit.get(player).substring(16))) {
            inv = Bukkit.createInventory(null, 45,
                    HexColor.PURPLE.toString() + ChatColor.BOLD + "SHOP UPGRADES "
                            + ChatColor.DARK_GRAY + Coins.getCoins(player) + " ⛃");
            addBackToMenuItem(inv);
        } else {
            inv = Bukkit.createInventory(null, 45,
                    HexColor.PURPLE.toString() + ChatColor.BOLD + "SHOP MENU");
        }

        for (ShopItem item : itemList) {
            // Create the itemStack for each item in the list.
            ItemStack itemStack = item.createItem(player);
            inv.setItem(item.getSlot(), itemStack);

            if (item.cost != 0) { // If it has no cost it also isn't air -> air has no displayname
                itemCost.put(itemStack.getItemMeta().getDisplayName(), item.cost); // Save the cost of the item.
            }
        }

        return inv;
    }

    private void addBackToMenuItem(Inventory inventory) {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(HexColor.RED + "Back to Menu");
        item.setItemMeta(meta);

        inventory.setItem(44, item);
    }
}
