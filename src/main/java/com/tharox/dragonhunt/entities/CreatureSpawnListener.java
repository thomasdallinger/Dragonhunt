package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.modifiers.classes.CaveSpiders;
import com.tharox.dragonhunt.modifiers.classes.ExplosiveCreepers;
import com.tharox.dragonhunt.modifiers.classes.ImmuneSkeletons;
import com.tharox.dragonhunt.modifiers.classes.PowerfulMobs;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CreatureSpawnListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        Location location = event.getLocation();
        World world = Bukkit.getServer().getWorld("world");

        if (world == null) {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "ERROR: World name is incorrect.");
            return;
        }

        if (CaveSpiders.active) {
            if (entity instanceof Spider) {
                if (!(entity instanceof CaveSpider)) {
                    event.setCancelled(true);
                    world.spawnEntity(location, EntityType.CAVE_SPIDER);
                }
            }
        }

        if (ExplosiveCreepers.active) {
            if (entity instanceof Creeper) {
                Creeper creeper = (Creeper) entity;
                creeper.setPowered(true);
            }
        }

        if (PowerfulMobs.active) {
            if (entity.getEquipment() != null) { // entity can have equipment
                // armor
                entity.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET, 1));
                entity.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
                entity.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
                entity.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS, 1));

                //weapon
                if (entity instanceof Skeleton) {
                    ItemStack bow = new ItemStack(Material.BOW);
                    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                    entity.getEquipment().setItemInMainHand(bow);
                } else {
                    entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE, 1));
                }
            }

            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .setBaseValue(entity.getHealth() + 10);
            entity.setHealth(entity.getHealth() + 10);
        }

        // SKELETONS and WITHER SKELETONS are invulnerable if the modifier is active
        if (ImmuneSkeletons.active) {
            if (entity instanceof Skeleton || entity instanceof WitherSkeleton) {
                entity.setInvulnerable(true);
            }
        }
    }
}
