package com.tharox.dragonhunt.brewing;

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

public class BrewingInventory {
    public Inventory brewingStand;
    public static final HashMap<String, Integer> itemCost = new HashMap<>();

    public BrewingInventory(FileConfiguration config, Player player) {
        brewingStand = createInventory(config, player);
    }

    private Inventory createInventory(FileConfiguration config, Player player) {
        ConfigurationSection itemsSection = config.getConfigurationSection("brewing");
        List<BrewingItem> itemList = new ArrayList<>();

        if (itemsSection == null) {
            Bukkit.getLogger().severe("Setup config.yml");
            return null;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);

            itemList.add(new BrewingItem(section));
        }

        Inventory inv = Bukkit.createInventory(null, 18,
                HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "BREWING STAND "
                        + ChatColor.LIGHT_PURPLE + Coins.getCoins(player) + " ⛃");

        for (BrewingItem item : itemList) {
            ItemStack itemStack = item.createItem(player); //create the itemStack for each item in the list
            inv.setItem(item.getSlot(), itemStack);

            itemCost.put(itemStack.getItemMeta().getDisplayName(), item.COST); //save the cost of the item
        }

        return inv;
    }
}
