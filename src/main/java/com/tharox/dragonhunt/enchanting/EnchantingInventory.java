package com.tharox.dragonhunt.enchanting;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnchantingInventory {
    public Inventory enchant;
    public static final HashMap<String, Integer> enchantmentCost = new HashMap<>();

    public EnchantingInventory(FileConfiguration config, Player player) {
        enchant = createInventory(config, player);
    }

    private Inventory createInventory(FileConfiguration config, Player player) {
        ConfigurationSection itemsSection = config.getConfigurationSection("enchanting");
        List<EnchantingItem> itemList = new ArrayList<>();

        if (itemsSection == null) {
            Bukkit.getLogger().severe("Setup config.yml");
            return null;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);

            itemList.add(new EnchantingItem(section));
        }

        Inventory inv = Bukkit.createInventory(null, 36, HexColor.HIGHLIGHT + "Enchant "
                + ChatColor.DARK_GRAY + Coins.getCoins(player) + " ⛃");

        for (EnchantingItem item : itemList) {
            ItemStack itemStack = item.createItem(player); //create the itemStack for each item in the list
            inv.setItem(item.getSlot(), itemStack);

            if (item.cost != 0) { //if it has no cost it also isn't air -> air has no displayname
                enchantmentCost.put(itemStack.getItemMeta().getDisplayName(), item.cost); //save the cost of the item
            }
        }

        return inv;
    }
}
