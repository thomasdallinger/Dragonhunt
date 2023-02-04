package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.modifiers.classes.PowderOnly;
import com.tharox.dragonhunt.coins.Coins;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Entity mob = event.getEntity();

        if (mob instanceof Player) return;

        if (killer != null) {
            if (event.getEntity().getName().equals("Ender Dragon"))
                new GameEnd(killer);

            if (mob instanceof Monster) {
                Coins.addXP(killer, Coins.monsterXP);
            }

            if (mob instanceof Animals || mob instanceof WaterMob) {
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKED_BEEF, 2));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.LEATHER, 1));
                Coins.addXP(killer, Coins.animalXP);
            }
        }

        event.setDroppedExp(0); // no natural XP orbs
        event.getDrops().clear();

        switch (mob.getName()) {
            case "Creeper":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.TNT, 1));
                break;
            case "Skeleton":
                ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                crossbow.addEnchantment(Enchantment.QUICK_CHARGE, 3);
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(crossbow));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.ARROW, 4));
                break;
            case "Piglin":
            case "Zombie":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.GOLD_INGOT, 2));
                break;
            case "Witch":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.DIAMOND, 2));
                break;
            case "Spider":
            case "Cave Spider":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.STRING, 3));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COBWEB));
                break;
            case "Enderman":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.ENDER_PEARL, 2));
                break;
            case "Blaze":
                if (killer != null)
                    if (PowderOnly.active)
                        GameState.putItemInInventory(killer, new ItemStack(Material.BLAZE_POWDER));
                    else
                        GameState.putItemInInventory(killer, new ItemStack(Material.BLAZE_ROD));
                break;
            case "Piglin Brute":
            case "Wither Skeleton":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.NETHERITE_SCRAP, 2));
                break;
            case "Iron Golem":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.IRON_INGOT, 5));
                break;
            default:
                break;
        }

        // Extra Drops (on top of food drops)
        if (mob.getName().equals("Sheep")) { // for crafting beds, bows
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.STRING, 8));
        } else if (mob.getName().equals("Chicken")) { // for crafting arrows
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.FEATHER, 2));
        }
    }
}
