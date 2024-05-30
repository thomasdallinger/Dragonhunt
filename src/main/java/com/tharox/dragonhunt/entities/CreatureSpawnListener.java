package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.modifiers.classes.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
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
        Random random = new Random();

        // High-Value Targets
        int levelMob = random.nextInt(15);
        if (levelMob == 0) {
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .setBaseValue(entity.getHealth() * 2);
            entity.setHealth(entity.getHealth() * 2);
            entity.setCustomName(ChatColor.WHITE.toString() + ChatColor.BOLD + "High-Value Target");
            entity.setCustomNameVisible(true);
        }

        /* return; statements are mostly avoided so it can be assured that every active modifier is in use */
        if (entity instanceof Mob) {
            if ((entity instanceof Ghast || entity instanceof PiglinBrute)) {
                event.setCancelled(true);
                return;
            }

            if (world.getName().equals("world_the_end") && !NoPillagers.active) {
                int spawnEnderman = random.nextInt(9) + 1;

                if (entity instanceof Enderman) {
                    if (spawnEnderman == 1) {
                        event.setCancelled(true);
                        spawnEndCreatures(world, location);
                        return;
                    }
                    // else spawn the Enderman
                }
            }

            // Spawn additional Endermans.
            int chance;

            if (EndermanParty.active) {
                chance = random.nextInt(5) + 1;
            } else {
                chance = random.nextInt(20) + 1;
            }

            // Endermans can replace all other mobs including Animals (but except water mobs)
            if (chance == 1 && !(entity instanceof Enderman) && !(entity instanceof WaterMob)) {
                event.setCancelled(true);
                world.spawnEntity(location, EntityType.ENDERMAN);
                return;
            }

            // Spawn Piglins or Magma Cubes instead of useless Zombified Piglins
            if (entity instanceof PigZombie) {
                event.setCancelled(true);
                chance = random.nextInt(5) + 1;

                if (chance == 1) {
                    world.spawnEntity(location, EntityType.MAGMA_CUBE);
                } else {
                    world.spawnEntity(location, EntityType.PIGLIN);
                }

                return;
            }

            // All Wither Skeletons have 2x health and iron swords
            if (entity instanceof WitherSkeleton) {
                Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .setBaseValue(entity.getHealth() * 2);
                entity.setHealth(entity.getHealth() * 2);
                Objects.requireNonNull(entity.getEquipment()).setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            }

            if (CaveSpiders.active) {
                if (entity instanceof Spider) {
                    if (!(entity instanceof CaveSpider)) {
                        event.setCancelled(true);
                        world.spawnEntity(location, EntityType.CAVE_SPIDER);
                    } else {
                        // Give all spawning Cave Spiders 3x health
                        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                                .setBaseValue(entity.getHealth() * 3);
                        entity.setHealth(entity.getHealth() * 3);
                    }
                }
            }

            if (SpeedyMobs.active) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50000, 1));
            }

            if (ExplosiveCreepers.active) {
                if (entity instanceof Creeper) {
                    Creeper creeper = (Creeper) entity;
                    creeper.setPowered(true);
                    creeper.setExplosionRadius(8);
                }
            }

            if (PowerfulMobs.active) {
                if (entity.getEquipment() != null) { // entity can have equipment
                    // Armor
                    entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
                    entity.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
                    entity.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
                    entity.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS, 1));

                    // Weapon
                    if (entity instanceof Skeleton) {
                        ItemStack bow = new ItemStack(Material.BOW);
                        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                        entity.getEquipment().setItemInMainHand(bow);
                    } else if (entity instanceof Pillager) {
                        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                        crossbow.addEnchantment(Enchantment.MULTISHOT, 1);
                        entity.getEquipment().setItemInMainHand(crossbow);
                    } else {
                        entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD, 1));
                    }
                }
            }

            if (FireMobs.active) {
                if (entity.getEquipment() != null) { // entity can have equipment
                    if (entity instanceof Skeleton) {
                        ItemStack bow = new ItemStack(Material.BOW);
                        bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
                        entity.getEquipment().setItemInMainHand(bow);
                    } else if (entity instanceof Pillager) {
                        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                        crossbow.addEnchantment(Enchantment.MULTISHOT, 1);
                        entity.getEquipment().setItemInMainHand(crossbow);
                    } else {
                        ItemStack sword = new ItemStack(Material.IRON_SWORD);
                        sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
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
                        sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                        sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                        sword.addEnchantment(Enchantment.KNOCKBACK, 2);
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

                        Material[] armorMaterials = {Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                                Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS};
                        EquipmentSlot[] equipmentSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                                EquipmentSlot.LEGS, EquipmentSlot.FEET};

                        for (int i = 0; i < armorMaterials.length; i++) {
                            ItemStack armorPiece = new ItemStack(armorMaterials[i]);
                            armorPiece.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                            entity.getEquipment().setItem(equipmentSlots[i], armorPiece);
                        }
                    }
                }
            }

            if (BrutalBlazes.active) {
                if (entity instanceof Blaze) {
                    Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                            .setBaseValue(entity.getHealth() * 5);
                    entity.setHealth(entity.getHealth() * 5);
                }
            }
        }
    }

    private LivingEntity spawnEndCreatures(World world, Location location) {
        Random random = new Random();
        int type = random.nextInt(10) + 1; // values 1-10

        EntityType entityType = EntityType.ENDERMAN;

        if (type <= 5) { // 50%
            entityType = EntityType.PILLAGER;
        }

        if (type >= 6 && type <= 8) { // 30%
            entityType = EntityType.VINDICATOR;
        }

        if (type >= 9) { // 20%
            entityType = EntityType.EVOKER;
        }

        return (LivingEntity) world.spawnEntity(location, entityType);
    }
}
