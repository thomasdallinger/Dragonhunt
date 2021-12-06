package com.tharox.dragonhunt.kits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;


import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class KitInventory {

    public Inventory kits;

    public KitInventory() {
        createInventory();
    }

    public void createInventory() {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Pick a Kit");
        AtomicInteger index = new AtomicInteger();

        Reflections reflections = new Reflections("com.tharox.dragonhunt");
        Set<Class<?>> kitClasses = reflections.getTypesAnnotatedWith(DragonhuntKit.class);

        kitClasses.forEach(
                (kitClass) -> {
                    if(Kit.class.isAssignableFrom(kitClass)) {
                        try {
                            inv.setItem(index.getAndIncrement(), Kit.getDisplayItem((String) kitClass.getField("title").get(null), (Material) kitClass.getField("material").get(null), (String) kitClass.getField("lore").get(null)));
                            //get(null) because static
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        kits = inv;
    }

}
