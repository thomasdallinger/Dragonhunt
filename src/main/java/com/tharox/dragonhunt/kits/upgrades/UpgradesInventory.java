package com.tharox.dragonhunt.kits.upgrades;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpgradesInventory {
    public Inventory upgrades;
    public static final HashMap<String, Integer> upgradeCost = new HashMap<>();

    public UpgradesInventory(FileConfiguration config, Player player) {
        upgrades = createInventory(config, player);
    }

    private Inventory createInventory(FileConfiguration config, Player player) {
        ConfigurationSection itemsSection = config.getConfigurationSection(
                GameState.selectedKit.get(player).substring(16));
        List<UpgradesItem> itemList = new ArrayList<>();

        if (itemsSection == null) {
            player.sendMessage(Prefixes.getErrorPrefix() + "Your Kit cannot be upgraded.");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return null;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);

            itemList.add(new UpgradesItem(section));
        }

        Inventory inv = Bukkit.createInventory(null, 27,
                GameState.selectedKit.get(player) + " " + ChatColor.DARK_GRAY + Coins.getCoins(player) + " ⛃");

        for (UpgradesItem item : itemList) {
            // Create the itemStack for each item in the list.
            ItemStack itemStack = item.createItem(player);
            inv.setItem(item.getSlot(), itemStack);

            if (item.cost != 0) { // If it has no cost it also isn't air -> air has no displayname.
                upgradeCost.put(itemStack.getItemMeta().getDisplayName(), item.cost); // Save the cost of the item.
            }
        }

        return inv;
    }
}
