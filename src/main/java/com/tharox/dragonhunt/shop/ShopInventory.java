package com.tharox.dragonhunt.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ShopInventory {

    public static Inventory menu;
    public static Inventory enchanting;
    public static Inventory foodPotionsArrows;
    public static Inventory blocksRedstone;
    public static final HashMap<String, Integer> ShopItemCost = new HashMap<>();

    public ShopInventory(FileConfiguration config) {
        menu = addItemsToList(config, "menu");
        enchanting = addItemsToList(config, "enchanting");
        foodPotionsArrows = addItemsToList(config, "foodPotionsArrows");
        blocksRedstone = addItemsToList(config, "blocksRedstone");
    }

    private Inventory addItemsToList(FileConfiguration config, String category) {
        ConfigurationSection itemsSection = config.getConfigurationSection(category);
        List<Item> itemList = new ArrayList<>();

        if (itemsSection == null) {
            Bukkit.getLogger().severe("Setup config.yml");
            return null;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);

            itemList.add(new Item(section));
        }

        if(category.equals("menu")) {
            return createMenu(itemList);
        }
        return createInventory(itemList);
    }

    private Inventory createMenu(List<Item> itemList) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD.toString() + ChatColor.BOLD + "Shop Menu");
        List<Integer> invPositions = Arrays.asList(10, 13, 16);

        for(int index = 0; index < itemList.size(); index++) {
            ItemStack itemStack = itemList.get(index).createItem(); //create the itemStack for each item in the list
            inv.setItem(invPositions.get(index), itemStack);
        }

        return inv;
    }

    private Inventory createInventory(List<Item> itemList) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD.toString() + ChatColor.BOLD + "Shop");

        for(int index = 0; index < itemList.size(); index++) {
            ItemStack itemStack = itemList.get(index).createItem(); //create the itemStack for each item in the list
            inv.setItem(index, itemStack);

            if(itemList.get(index).cost != 0) { //if it has no cost it also isn't air -> air has no displayname
                ShopItemCost.put(itemStack.getItemMeta().getDisplayName(), itemList.get(index).cost); //save the cost of the item
            }
        }

        //Add "Back to menu" item to inventory
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.RED + "Back to menu");
        item.setItemMeta(meta);

        inv.setItem(26, item);
        return inv;
    }
}
