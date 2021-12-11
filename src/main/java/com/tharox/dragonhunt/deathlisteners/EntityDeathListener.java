package com.tharox.dragonhunt.deathlisteners;

import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.points.Points;
import org.bukkit.Material;
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
        int points = 20;

        if (killer != null) {
            if (event.getEntity().getName().equals("Ender Dragon")) {
                new GameEnd(killer);
            }

            if (mob instanceof Monster) {
                Points.addPoints(killer, points);
            }

            if (mob instanceof Animals) {
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKED_PORKCHOP, 2));
            }
        }

        event.getDrops().clear();

        int chance = (int) (Math.random() * 8 + 1);

        switch (mob.getName()) {
            case "Creeper":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.GUNPOWDER, 4));
                if (chance == 1)
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.TNT, 1));
                break;
            case "Skeleton":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.ARROW, 4));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.BONE, 2));
                if (chance == 1)
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.CROSSBOW, 1));
                break;
            case "Zombie":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.GOLD_INGOT, 2));
                if (chance == 1)
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.APPLE, 1));
                break;
            case "Spider":
            case "Cave Spider":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.STRING, 3));
                if (chance == 1)
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COBWEB, 3));
                break;
            case "Enderman":
                setInventoryItem(killer, mob, new ItemStack(Material.ENDER_PEARL, 2));
                break;
            case "Blaze":
                setInventoryItem(killer, mob, new ItemStack(Material.BLAZE_ROD, 1));
                break;
            default:
                break;
        }

        //Extra Drops
        if (mob.getName().equals("Hoglin")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKED_PORKCHOP, 2));
        } else if (mob.getName().equals("Sheep")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.STRING, 8));
        } else if (mob.getName().equals("Chicken")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.FEATHER, 2));
        } else if (mob.getName().equals("Iron Golem")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.IRON_INGOT, 4));
        }
    }

    private void setInventoryItem(Player killer, Entity mob, ItemStack item) {
        if (killer != null) {
            if(GameState.sameItemAlreadyInInventory(killer, item)) return;

            if (killer.getInventory().firstEmpty() == -1) { //Inventory full
                mob.getWorld().dropItemNaturally(mob.getLocation(), item);
            } else {
                killer.getInventory().addItem(item);
            }
        }
    }
}
