package com.tharox.dragonhunt.entities;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
                    item.setItemStack(new ItemStack(Material.GRAVEL, 7));
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
                    item.setItemStack(new ItemStack(Material.SOUL_SOIL, 9));
                    break;
                case 6:
                    item.setItemStack(new ItemStack(Material.OBSIDIAN, 2));
                    break;
                case 8:
                    item.setItemStack(new ItemStack(Material.ENDER_PEARL, 2));
                    break;
                case 9:
                    item.setItemStack(new ItemStack(Material.SPECTRAL_ARROW, 3));
                    break;
                case 10:
                    item.setItemStack(createPotion());
                    break;
            }
        }
    }

    public ItemStack createPotion() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0),
                true);
        meta.setColor(Color.ORANGE);
        meta.setDisplayName(ChatColor.YELLOW + "Potion of Fire Resistance");
        item.setItemMeta(meta);

        return item;
    }
}
