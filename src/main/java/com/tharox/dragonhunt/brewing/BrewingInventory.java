package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
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

public class BrewingInventory {
    public Inventory menu, permanentEffects, arrowEffects;
    public static final HashMap<String, Integer> effectCost = new HashMap<>();

    public BrewingInventory(FileConfiguration config, Player player) {
        menu = createInventory(config, "brewing", player);
        permanentEffects = createInventory(config, "permanentEffects", player);
        arrowEffects = createInventory(config, "negativeEffects", player);
    }

    private Inventory createInventory(FileConfiguration config, String category, Player player) {
        ConfigurationSection itemsSection = config.getConfigurationSection(category);
        List<BrewingItem> itemList = new ArrayList<>();

        if (itemsSection == null) {
            Bukkit.getLogger().severe("Setup config.yml");
            return null;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);

            itemList.add(new BrewingItem(section));
        }

        Inventory inv;
        if (!category.equals("brewing")) {
            inv = Bukkit.createInventory(null, 27, HexColor.HIGHLIGHT + "Brewing Stand "
                    + ChatColor.DARK_GRAY + Coins.getCoins(player) + " ⛃");
            addBackToMenuItem(inv);
        } else {
            inv = Bukkit.createInventory(null, 27, HexColor.HIGHLIGHT + "Brewing Stand Menu");
        }

        for (BrewingItem item : itemList) {
            ItemStack itemStack = item.createItem(player); // create the itemStack for each item in the list
            inv.setItem(item.getSlot(), itemStack);

            if (item.cost != 0) { // if it has no cost it also isn't air -> air has no displayname
                effectCost.put(itemStack.getItemMeta().getDisplayName(), item.cost); // save the cost of the item
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
        meta.setDisplayName(HexColor.RED + "Back To Menu");
        item.setItemMeta(meta);

        inventory.setItem(26, item);
    }
}
