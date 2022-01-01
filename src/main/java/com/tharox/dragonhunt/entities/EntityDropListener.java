package com.tharox.dragonhunt.entities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDropListener implements Listener {

    @EventHandler
    public void onMobDrop(EntityDropItemEvent event) {
        Entity mob = event.getEntity();
        Item item = event.getItemDrop();

        int chance = (int) (Math.random() * 8 + 1);

        if (mob.getName().equals("Piglin")) {

            if (chance >= 1 && chance <= 3) {
                item.setItemStack(new ItemStack(Material.SPECTRAL_ARROW, 3));
            } else if (chance == 4 || chance == 5) {
                item.setItemStack(new ItemStack(Material.GRAVEL, 4));
            } else if (chance == 6 || chance == 7) {
                item.setItemStack(new ItemStack(Material.APPLE));
            } else if (chance == 8) {
                item.setItemStack(new ItemStack(Material.ENDER_PEARL, 1));
            }
        }
    }
}
