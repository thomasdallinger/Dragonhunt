package com.tharox.dragonhunt.entities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDropListener implements Listener {
    public Integer dropIndex = 0; // Starts at 0 but is incremented on the first event call.
    public static String nextDrop = "GLOWSTONE"; // Drop with dropIndex 1.

    @EventHandler
    public void onMobDrop(EntityDropItemEvent event) {
        Entity mob = event.getEntity();
        Item item = event.getItemDrop();

        if (mob.getName().equals("Piglin")) {
            dropIndex++;
            if (dropIndex > 10)
                dropIndex = 1;

            item.setItemStack(getDrop(dropIndex));

            if ((dropIndex + 1) > 10) {
                nextDrop = getDrop(dropIndex - 9).getType().toString().replace("_", " ");
            } else {
                nextDrop = getDrop(dropIndex + 1).getType().toString().replace("_", " ");
            }

        }
    }

    public ItemStack getDrop(int dropIndex) {
        ItemStack itemToDrop;

        switch (dropIndex) {
            case 1:
                itemToDrop = new ItemStack(Material.COOKED_BEEF, 2);
                break;
            case 2:
                itemToDrop = new ItemStack(Material.GOLDEN_APPLE);
                break;
            case 3:
                itemToDrop = new ItemStack(Material.ENDER_PEARL);
                break;
            case 4:
                itemToDrop = new ItemStack(Material.BOOK);
                break;
            case 5:
                itemToDrop = new ItemStack(Material.BLACKSTONE, 12);
                break;
            case 6:
                itemToDrop = new ItemStack(Material.OBSIDIAN, 2);
                break;
            case 7:
                itemToDrop = new ItemStack(Material.DIAMOND, 2);
                break;
            case 8:
                itemToDrop = new ItemStack(Material.ENDER_PEARL, 2);
                break;
            case 9:
                itemToDrop = new ItemStack(Material.BOW);
                break;
            case 10:
                itemToDrop = new ItemStack(Material.NETHERITE_SCRAP, 2);
                break;
            default:
                // Unexpected Value.
                itemToDrop = new ItemStack(Material.AIR);
        }

        return itemToDrop;
    }
}
