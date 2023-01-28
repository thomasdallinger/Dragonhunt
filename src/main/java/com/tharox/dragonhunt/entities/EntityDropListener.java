package com.tharox.dragonhunt.entities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDropListener implements Listener {
    public Integer totalPiglinTrades = 0;

    @EventHandler
    public void onMobDrop(EntityDropItemEvent event) {
        Entity mob = event.getEntity();
        Item item = event.getItemDrop();

        if (mob.getName().equals("Piglin")) {
            totalPiglinTrades++;
            if (totalPiglinTrades > 10)
                totalPiglinTrades = 1;

            switch (totalPiglinTrades) {
                case 1:
                    item.setItemStack(new ItemStack(Material.GLOWSTONE_DUST, 12));
                    break;
                case 2:
                case 7:
                    item.setItemStack(new ItemStack(Material.STRING, 4));
                    break;
                case 3:
                    item.setItemStack(new ItemStack(Material.ENDER_PEARL));
                    break;
                case 4:
                    item.setItemStack(new ItemStack(Material.CRYING_OBSIDIAN, 3));
                    break;
                case 5:
                    item.setItemStack(new ItemStack(Material.BLACKSTONE, 9));
                    break;
                case 6:
                    item.setItemStack(new ItemStack(Material.OBSIDIAN, 2));
                    break;
                case 8:
                    item.setItemStack(new ItemStack(Material.ENDER_PEARL, 2));
                    break;
                case 9:
                    item.setItemStack(new ItemStack(Material.FLINT, 6));
                    break;
                case 10:
                    item.setItemStack(new ItemStack(Material.FEATHER, 6));
                    break;
            }
        }
    }
}
