package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.modifiers.classes.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class CreatureSpawnListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        Location location = event.getLocation();
        World world = entity.getWorld();

        if (entity instanceof Mob) {
            if (world.getName().equals("world_the_end")) {
                Random random = new Random();
                int spawnEnderman = random.nextInt(3) + 1; // values 1-3

                if (entity instanceof Enderman) {
                    if (spawnEnderman == 3) {
                        event.setCancelled(true);
                        spawnEndCreatures(world, location);
                    }
                    // else spawn the Enderman
                }
            }

            if (SpeedyMobs.active) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50000, 0));
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
                    entity.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
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
                        .setBaseValue(entity.getHealth() + 6); // set max health to a higher value
                entity.setHealth(entity.getHealth() + 6);
            }

            if (FireMobs.active) {
                if (entity.getEquipment() != null) { // entity can have equipment
                    if (entity instanceof Skeleton) {
                        ItemStack bow = new ItemStack(Material.BOW);
                        bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
                        entity.getEquipment().setItemInMainHand(bow);
                    } else {
                        ItemStack sword = new ItemStack(Material.IRON_SWORD);
                        sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
                        entity.getEquipment().setItemInMainHand(sword);
                    }
                }

                Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .setBaseValue(entity.getHealth() + 6);
                entity.setHealth(entity.getHealth() + 6);
            }

            // SKELETONS and WITHER SKELETONS are invulnerable if the modifier is active
            if (ImmuneSkeletons.active) {
                if (entity instanceof Skeleton || entity instanceof WitherSkeleton) {
                    equipNetheriteArmor(entity);
                    entity.setInvulnerable(true);
                }
            }
        }
    }

    /**
     * Gives the provided entity a fully enchanted netherite armor.
     */
    private void equipNetheriteArmor(LivingEntity entity) {
        // not possible if the entity can't have any equipment
        if (entity.getEquipment() == null) return;

        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        entity.getEquipment().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        entity.getEquipment().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        entity.getEquipment().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        entity.getEquipment().setBoots(boots);
    }

    private LivingEntity spawnEndCreatures(World world, Location location) {
        Random random = new Random();
        int type = random.nextInt(8) + 1; // values 1-8

        EntityType entityType = EntityType.ENDERMAN;

        if (type <= 4) { // 50%
            entityType = EntityType.PILLAGER;
        }

        if (type >= 5 && type <= 6) { // 25%
            entityType = EntityType.VINDICATOR;
        }

        if (type >= 7) { // 25%
            entityType = EntityType.EVOKER;
        }

        return (LivingEntity) world.spawnEntity(location, entityType);
    }
}
