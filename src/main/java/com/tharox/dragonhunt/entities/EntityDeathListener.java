package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.game.GameEnd;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.upgrades.XPUpgrade;
import com.tharox.dragonhunt.points.UpgradePoints;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
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
                killer.giveExp(UpgradePoints.monsterXP + XPUpgrade.MoreXP.get(killer) - 1);
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }

            if (mob instanceof Animals) {
                mob.getWorld().dropItemNaturally(mob.getLocation(), GameState.food.get(killer));
                killer.giveExp(UpgradePoints.animalXP + XPUpgrade.MoreXP.get(killer) - 1);
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
        }

        event.setDroppedExp(0); // no natural XP orbs
        event.getDrops().clear();

        switch (mob.getName()) {
            case "Creeper":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.TNT, 1));
                break;
            case "Skeleton":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.ARROW, 4));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.CROSSBOW));
                break;
            case "Piglin":
            case "Zombie":
                if(GameState.resources.get(killer).get(1) != null)
                    mob.getWorld().dropItemNaturally(mob.getLocation(), GameState.resources.get(killer).get(1));
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
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.BLAZE_ROD, 1));
                break;
            case "Piglin Brute":
            case "Wither Skeleton":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.NETHERITE_INGOT));
                break;
            default:
                break;
        }

        // Extra Drops (on top of normal drops)
        if (mob.getName().equals("Hoglin")) { // drops double food
            if(GameState.food.get(killer) != null)
                mob.getWorld().dropItemNaturally(mob.getLocation(), GameState.food.get(killer));
        } else if (mob.getName().equals("Sheep")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.STRING, 8));
        } else if (mob.getName().equals("Chicken")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.FEATHER, 2));
        } else if (mob.getName().equals("Iron Golem")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.IRON_INGOT, 5));
        }
    }
}
