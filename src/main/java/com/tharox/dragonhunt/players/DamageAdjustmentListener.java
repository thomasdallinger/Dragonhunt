package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.classes.KitClass;
import com.tharox.dragonhunt.kits.classes.Poisoner;
import com.tharox.dragonhunt.upgrades.Enchantments;
import com.tharox.dragonhunt.upgrades.Upgrades;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Random;

/**
 * The first damage assignment resets the vanilla values.
 */
public class DamageAdjustmentListener implements Listener {
    public static double mutantDamageBuff = 1.0;
    public static HashMap<Player, Double> absorbedDamage = new HashMap<>();

    // Assassin passive
    private static final double HEALTH_BACK_CHANCE = 0.20;  // 20% chance to release a smoke bomb
    private Random random = new Random();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Projectile projectile; // includes all types of arrows and fireworks
        ProjectileSource shooter;
        Player damager;
        Entity entity = event.getEntity();
        double damage = event.getDamage();

        if (event.getDamager() instanceof Projectile) {
            projectile = (Projectile) event.getDamager();
            shooter = projectile.getShooter();

            if (shooter instanceof Player) {
                damager = (Player) shooter;
            } else return;

        } else if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else return;

        // Kit Ability: Attack Damage increase
        if (GameState.selectedKit.containsKey(damager) && GameState.kitAbilityActive.get(damager) > 0) {
            if (GameState.selectedKit.get(damager).contains("Mutant")) {
                damage = damage + mutantDamageBuff;
            }

            if (GameState.selectedKit.get(damager).contains("Warrior")) {
                if (damager.getItemInUse() != null
                        && Enchantments.meleeWeapons.contains(damager.getItemInUse().getType())) {
                    damage = damage * 2;
                }
            }

            if (GameState.selectedKit.get(damager).contains("Sniper")) {
                if (damager.getInventory().getItemInMainHand().getType().equals(Material.BOW)
                        || damager.getInventory().getItemInMainHand().getType().equals(Material.CROSSBOW)) {
                    damage = damage * Upgrades.Effectiveness.getOrDefault(damager, 5);
                    // End the Kit ability
                    GameState.kitAbilityActive.put(damager, 0);

                    // Visuals & Audio
                    World world = damager.getWorld();
                    Location location = damager.getLocation();
                    world.spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 10);
                    world.spawnParticle(Particle.SMOKE_LARGE, entity.getLocation(), 50);
                    damager.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    damager.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 1.0f);
                }
            }

            if (GameState.selectedKit.get(damager).contains("Poisoner")) {
                int amplifier = Poisoner.EFFECTAMPLIFIER + Upgrades.Effectiveness.getOrDefault(damager, 0);
                int duration = Poisoner.EFFECTDURATION + Upgrades.Effectiveness2.getOrDefault(damager, 0);

                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                            duration * 20, amplifier - 1));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                            duration * 20, 0));
                }

                // Visuals & Audio
                World world = damager.getWorld();
                Location location = damager.getLocation();
                world.spawnParticle(Particle.SPELL_MOB, entity.getLocation(), 10);
                world.spawnParticle(Particle.END_ROD, entity.getLocation(), 50);
                damager.playSound(location, Sound.ENTITY_EVOKER_FANGS_ATTACK, 1.0f, 1.0f);
                damager.playSound(location, Sound.ENTITY_GUARDIAN_HURT, 1.0f, 1.0f);
            }
        }

        // If an Absorber's Kit ability is not active
        if (GameState.selectedKit.containsKey(damager) && GameState.selectedKit.get(damager).contains("Absorber")
                && GameState.kitAbilityActive.get(damager) == 0) {
            damage = damage + absorbedDamage.getOrDefault(damager, 0.0);
            absorbedDamage.put(damager, 0.0); // Reset the absorbedDamage after applying it

            // Visuals & Audio
            World world = damager.getWorld();
            Location location = damager.getLocation();
            world.spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 10);
            world.spawnParticle(Particle.SMOKE_LARGE, entity.getLocation(), 50);
            damager.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            damager.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 1.0f);
        }

        event.setDamage(damage);
    }

    @EventHandler
    public void onPlayerTakesDamage(EntityDamageByEntityEvent event) {
        Player player;
        double damage = event.getDamage();

        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
        } else return;

        if (GameState.selectedKit.containsKey(player) && GameState.kitAbilityActive.get(player) > 0) {
            // Add damage to absorbedDamage while the ability is active
            if (GameState.selectedKit.get(player).contains("Absorber")) {
                damage = damage * Upgrades.Effectiveness.getOrDefault(player, 1);

                absorbedDamage.put(player, absorbedDamage.getOrDefault(player, 0.0) + damage);
            }
        }

        if (KitClass.assassins.contains(player)) {
            if (random.nextDouble() < HEALTH_BACK_CHANCE) {
                // Only give health back if possible (player health must be between 0.0 and 20.0)
                if ((player.getHealth() + (damage / 2)) <= 20.0) {
                    player.setHealth(player.getHealth() + (damage / 2));
                }
            }
        }
    }
}
