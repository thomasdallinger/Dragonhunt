package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.upgrades.Upgrades;
import com.tharox.dragonhunt.modifiers.classes.ChristmasCookies;
import com.tharox.dragonhunt.modifiers.classes.MoreFood;
import com.tharox.dragonhunt.modifiers.classes.RevivedMobs;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Entity mob = event.getEntity();

        if (mob instanceof Player) return;

        // Cleric Kit Effectiveness
        int bonusXP = 0;

        if (Upgrades.Effectiveness.getOrDefault(killer, 0) != 0
                && GameState.selectedKit.getOrDefault(killer, "").contains("Cleric")) {
            bonusXP = Upgrades.Effectiveness.get(killer);
        }

        if (killer != null) {
            if (mob instanceof Monster) {
                Coins.addXP(killer, Coins.monsterXP + bonusXP);

                if (RevivedMobs.active) {
                    Random random = new Random();
                    int chance = random.nextInt(8) + 1;

                    if (chance == 1)
                        mob.getWorld().spawnEntity(mob.getLocation(), mob.getType());
                }
            }

            if (mob instanceof Animals || mob instanceof WaterMob) {
                if (MoreFood.active) {
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKED_BEEF, 4));
                } else if (ChristmasCookies.active) {
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKIE, 2));
                } else {
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKED_BEEF, 2));
                }

                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.LEATHER, 1));
                Coins.addXP(killer, Coins.animalXP + bonusXP);
            }
        }

        event.setDroppedExp(0); // no natural XP orbs
        event.getDrops().clear(); // clear all natural drops

        switch (mob.getName()) {
            case "Creeper":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.TNT, 3));
                break;
            case "Skeleton":
            case "Stray":
                Random random = new Random();

                if (random.nextBoolean()) {
                    ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                    crossbow.addEnchantment(Enchantment.QUICK_CHARGE, 3);
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(crossbow));
                } else {
                    mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.IRON_INGOT, 3));
                }
                break;
            case "Zombie":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.APPLE, 2));
                break;
            case "Piglin":
            case "Zombified Piglin":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKED_BEEF, 3));
                break;
            case "Witch":
            case "Pillager":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.GOLD_INGOT, 4));
                break;
            case "Hoglin":
            case "Zoglin":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COOKED_BEEF, 3));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.GOLD_INGOT, 4));
                break;
            case "Vindicator":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.DIAMOND, 2));
                break;
            case "Spider":
            case "Cave Spider":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.STRING, 3));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.COBWEB));
                break;
            case "Enderman":
                if (killer != null) {
                    GameState.putItemInInventory(killer, new ItemStack(Material.ENDER_PEARL, 2));
                }
                break;
            case "Blaze":
                if (killer != null) {
                    GameState.putItemInInventory(killer, new ItemStack(Material.BLAZE_ROD));
                }
                break;
            case "Iron Golem":
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.IRON_INGOT, 8));
                break;
            case "Evoker":
            case "Wither Skeleton":
                mob.getWorld().dropItemNaturally(mob.getLocation(),
                        Objects.requireNonNull(GameState.createRockets(3)));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.NETHERITE_SCRAP, 2));
                break;
            case "Ravager":
            case "Piglin Brute":
                mob.getWorld().dropItemNaturally(mob.getLocation(),
                        Objects.requireNonNull(GameState.createRockets(3)));
                mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.NETHERITE_INGOT));
                break;
            default:
                break;
        }

        // Extra Drops (on top of food drops)
        if (mob.getName().equals("Sheep")) { // for crafting beds, bows
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.STRING, 8));
        }

        // Blacksmith Kit Effectiveness
        if (Upgrades.Effectiveness.getOrDefault(killer, 0) != 0
                && GameState.selectedKit.getOrDefault(killer, "").contains("Blacksmith")) {
            mob.getWorld().dropItemNaturally(mob.getLocation(), new ItemStack(Material.GOLD_INGOT,
                    Upgrades.Effectiveness.get(killer)));
        }
    }
}
