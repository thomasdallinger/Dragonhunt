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

        /* return; statements are avoided so it can be assured that every active modifier is in use */
        if (entity instanceof Mob) {
            if (entity instanceof Ghast && NoGhasts.active) {
                event.setCancelled(true);
            }

            if (entity instanceof PiglinBrute && NoBrutes.active) {
                event.setCancelled(true);
            }

            if (world.getName().equals("world_the_end") && !NoPillagers.active) {
                Random random = new Random();
                int spawnEnderman = random.nextInt(9) + 1;

                if (entity instanceof Enderman) {
                    if (spawnEnderman == 1) {
                        event.setCancelled(true);
                        spawnEndCreatures(world, location);
                    }
                    // else spawn the Enderman
                }
            }

            if (EndermanParty.active) {
                Random random = new Random();
                int chance = random.nextInt(20) + 1;

                if (chance == 1 && entity instanceof Monster) {
                    event.setCancelled(true);
                    world.spawnEntity(location, EntityType.ENDERMAN);
                }
            }

            if (CaveSpiders.active) {
                if (entity instanceof Spider) {
                    if (!(entity instanceof CaveSpider)) {
                        event.setCancelled(true);
                        world.spawnEntity(location, EntityType.CAVE_SPIDER);
                    }
                }
            }

            if (SpeedyMobs.active) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50000, 0));
            }

            if (ExplosiveCreepers.active) {
                if (entity instanceof Creeper) {
                    Creeper creeper = (Creeper) entity;
                    creeper.setPowered(true);
                }
            }

            if (PowerfulMobs.active) {
                if (entity.getEquipment() != null) { // entity can have equipment
                    // Armor
                    entity.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
                    entity.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
                    entity.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
                    entity.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));

                    // Weapon
                    if (entity instanceof Skeleton) {
                        ItemStack bow = new ItemStack(Material.BOW);
                        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                        entity.getEquipment().setItemInMainHand(bow);
                    } else {
                        entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE, 1));
                    }
                }
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
            }

            // SKELETONS and WITHER SKELETONS are invulnerable if the modifier is active
            if (ImmuneSkeletons.active) {
                if (entity instanceof Skeleton || entity instanceof WitherSkeleton) {
                    // not possible if the entity can't have any equipment
                    if (entity.getEquipment() != null) {
                        entity.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
                        entity.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
                        entity.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
                        entity.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
                    }

                    entity.setInvulnerable(true);
                }
            }

            if (WitherSkeletons.active) {
                if (entity instanceof WitherSkeleton) {
                    if (entity.getEquipment() != null) {
                        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
                        sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                        entity.getEquipment().setItemInMainHand(sword);
                    }
                }
            }

            if (BrutalBrutes.active) {
                if (entity instanceof PiglinBrute) {
                    if (entity.getEquipment() != null) {
                        ItemStack sword = new ItemStack(Material.NETHERITE_AXE);
                        sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                        entity.getEquipment().setItemInMainHand(sword);
                    }
                }
            }

            if (BrutalBlazes.active) {
                if (entity instanceof Blaze) {
                    Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                            .setBaseValue(entity.getHealth() * 3);
                    entity.setHealth(entity.getHealth() * 3);
                }
            }
        }
    }

    private LivingEntity spawnEndCreatures(World world, Location location) {
        Random random = new Random();
        int type = random.nextInt(10) + 1; // values 1-10

        EntityType entityType = EntityType.ENDERMAN;

        if (type <= 4) { // 40%
            entityType = EntityType.PILLAGER;
        }

        if (type >= 5 && type <= 7) { // 30%
            entityType = EntityType.VINDICATOR;
        }

        if (type >= 8 && type <= 9) { // 20%
            entityType = EntityType.EVOKER;
        }

        if (type >= 10) { // 10%
            entityType = EntityType.RAVAGER;
        }

        return (LivingEntity) world.spawnEntity(location, entityType);
    }
}
