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
                itemToDrop = new ItemStack(Material.GLOWSTONE, 3);
                break;
            case 2:
                itemToDrop = new ItemStack(Material.APPLE, 3);
                break;
            case 3:
                itemToDrop = new ItemStack(Material.ENDER_PEARL);
                break;
            case 4:
                itemToDrop = new ItemStack(Material.CRYING_OBSIDIAN, 3);
                break;
            case 5:
                itemToDrop = new ItemStack(Material.BLACKSTONE, 12);
                break;
            case 6:
                itemToDrop = new ItemStack(Material.OBSIDIAN, 2);
                break;
            case 7:
                itemToDrop = createPotion(true, PotionEffectType.HARM, Color.MAROON,
                        "Harming");
                break;
            case 8:
                itemToDrop = new ItemStack(Material.ENDER_PEARL, 2);
                break;
            case 9:
                itemToDrop = new ItemStack(Material.STRING, 3);
                break;
            case 10:
                itemToDrop = createPotion(false, PotionEffectType.FIRE_RESISTANCE, Color.ORANGE,
                        "Fire Resistance");
                break;
            default:
                // Unexpected Value.
                itemToDrop = new ItemStack(Material.AIR);
        }

        return itemToDrop;
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
