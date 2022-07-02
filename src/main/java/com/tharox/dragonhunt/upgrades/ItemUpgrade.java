package com.tharox.dragonhunt.upgrades;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemUpgrade {
    public Player player;
    public int level;

    public void replaceItem(Material oldItem, Material newItem) {
        int amount;
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (item.getType().equals(oldItem)) {
                    amount = item.getAmount();
                    item.setType(newItem); //resets item data -> amount needs to set again
                    item.setAmount(amount);
                }
            }
        }
    }
}
