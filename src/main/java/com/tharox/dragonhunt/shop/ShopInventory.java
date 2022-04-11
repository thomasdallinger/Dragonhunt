package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.points.Points;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public Inventory menu, weapon, bow, crossbow, arrow, tool, armor, food;
    public static final HashMap<String, Integer> ShopItemCost = new HashMap<>();

    public ShopInventory(FileConfiguration config, Player player) {
        menu = createInventory(config, "menu", player);
        weapon = createInventory(config, "weapon", player);
        bow = createInventory(config, "bow", player);
        crossbow = createInventory(config, "crossbow", player);
        arrow = createInventory(config, "arrow", player);
        tool = createInventory(config, "tool", player);
        armor = createInventory(config, "armor", player);
        food = createInventory(config, "food", player);
    }

    private Inventory createInventory(FileConfiguration config, String category, Player player) {
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

        Inventory inv;
        if(!category.equals("menu")) {
            inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "GEAR UPGRADES "
                    + ChatColor.LIGHT_PURPLE + Points.getPoints(player) + " Points");
            addBackToMenuItem(inv);
        } else {
            inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "GEAR UPGRADES MENU");
        }

        for (Item item : itemList) {
            ItemStack itemStack = item.createItem(player); //create the itemStack for each item in the list
            inv.setItem(item.getSlot(), itemStack);

            if (item.cost != 0) { //if it has no cost it also isn't air -> air has no displayname
                ShopItemCost.put(itemStack.getItemMeta().getDisplayName(), item.cost); //save the cost of the item
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
        meta.setDisplayName(ChatColor.RED + "Back to menu");
        item.setItemMeta(meta);

        inventory.setItem(26, item);
    }
}
