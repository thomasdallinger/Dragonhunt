package com.tharox.dragonhunt.entities;

import net.md_5.bungee.api.ChatColor;
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
                    item.setItemStack(new ItemStack(Material.GLOWSTONE, 3));
                    break;
                case 2:
                    item.setItemStack(new ItemStack(Material.BOW));
                    break;
                case 3:
                    item.setItemStack(new ItemStack(Material.ENDER_PEARL));
                    break;
                case 4:
                    item.setItemStack(new ItemStack(Material.CRYING_OBSIDIAN, 3));
                    break;
                case 5:
                    item.setItemStack(new ItemStack(Material.BLACKSTONE, 12));
                    break;
                case 6:
                    item.setItemStack(new ItemStack(Material.OBSIDIAN, 2));
                    break;
                case 7:
                    item.setItemStack(createPotion(true, PotionEffectType.HARM, Color.MAROON,
                            "Harming"));
                    break;
                case 8:
                    item.setItemStack(new ItemStack(Material.ENDER_PEARL, 2));
                    break;
                case 9:
                    item.setItemStack(new ItemStack(Material.FLINT_AND_STEEL));
                    break;
                case 10:
                    item.setItemStack(createPotion(false, PotionEffectType.FIRE_RESISTANCE, Color.ORANGE,
                            "Fire Resistance"));
                    break;
            }
        }
    }

    public ItemStack createPotion(boolean splash, PotionEffectType effect, Color color, String title) {
        ItemStack item;
        String potionType;
        if (splash) {
            item = new ItemStack(Material.SPLASH_POTION, 2);
            potionType = "Splash Potion of ";
        } else {
            item = new ItemStack(Material.POTION);
            potionType = "Potion of ";
        }

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        meta.addCustomEffect(new PotionEffect(effect, 1200, 0),
                true);
        meta.setColor(color);

        meta.setDisplayName(ChatColor.AQUA + potionType + title);
        item.setItemMeta(meta);

        return item;
    }
}
