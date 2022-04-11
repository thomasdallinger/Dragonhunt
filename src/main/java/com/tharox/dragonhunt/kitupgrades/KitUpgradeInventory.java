package com.tharox.dragonhunt.kitupgrades;

import com.tharox.dragonhunt.points.Points;
import com.tharox.dragonhunt.shop.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KitUpgradeInventory {

    public Inventory platformer, skydiver, trapcrafter, vanisher;
    public static final HashMap<String, Integer> KitUpgradeCost = new HashMap<>();

    public KitUpgradeInventory(FileConfiguration config, Player player) {
        platformer = createInventory(config, "platformer", player);
        skydiver = createInventory(config, "skydiver", player);
        trapcrafter = createInventory(config, "trapcrafter", player);
        vanisher = createInventory(config, "vanisher", player);
    }

    private Inventory createInventory(FileConfiguration config, String kit, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "KIT UPGRADES "
                        + ChatColor.GREEN + Points.getPoints(player) + " Points");

        String[] categories = {kit, "cooldown", "quickcharge", "duration"};
        for (String category : categories) {
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

            for (Item item : itemList) {
                ItemStack itemStack = item.createItem(player); //create the itemStack for each item in the list
                inv.setItem(item.getSlot(), itemStack);

                if (item.cost != 0) { //if it has no cost it also isn't air -> air has no displayname
                    KitUpgradeCost.put(itemStack.getItemMeta().getDisplayName(), item.cost); //save the cost of the item
                }
            }
        }

        return inv;
    }
}
