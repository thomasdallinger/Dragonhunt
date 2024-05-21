package com.tharox.dragonhunt.entities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EntityDropListener implements Listener {

    @EventHandler
    public void onMobDrop(EntityDropItemEvent event) {
        Entity mob = event.getEntity();
        Item item = event.getItemDrop();
        Random random = new Random();

        if (mob.getName().equals("Piglin")) {
            item.setItemStack(getDrop(random.nextInt(5) + 1));
        }
    }

    public ItemStack getDrop(int dropIndex) {
        ItemStack itemToDrop;
        Random amount = new Random();

        switch (dropIndex) {
            case 1:
                itemToDrop = new ItemStack(Material.GOLDEN_APPLE, amount.nextInt(3) + 1);
                break;
            case 2:
                itemToDrop = new ItemStack(Material.ENDER_PEARL, amount.nextInt(3) + 2);
                break;
            case 3:
                itemToDrop = new ItemStack(Material.DIAMOND, amount.nextInt(3) + 1);
                break;
            case 4:
                itemToDrop = new ItemStack(Material.FIREWORK_ROCKET, amount.nextInt(6) + 1);
                break;
            case 5:
                itemToDrop = new ItemStack(Material.NETHERITE_INGOT);
                break;
            default:
                // Unexpected Value.
                itemToDrop = new ItemStack(Material.AIR);
        }

        return itemToDrop;
    }
}
