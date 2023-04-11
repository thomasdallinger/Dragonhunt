package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.enchanting.Enchantments;
import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Collection;

/**
 * The first damage assignment resets the vanilla values.
 */
public class DamageAdjustmentListener implements Listener {
    public static double mutantDamageBuff = 1.0;

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

        // Nerf strength.
        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            Collection<PotionEffect> pe = damager.getActivePotionEffects();
            for (PotionEffect effect : pe) {
                if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    damage = damage - effect.getAmplifier() * 1.5; // Remove default damage increase.
                    damage = damage + effect.getAmplifier() * 0.5; // Add new damage increase.
                }
            }
        }

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
                    damage = damage * 3;
                }
            }
        }

        // Nerf resistance.
        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                Collection<PotionEffect> pe = player.getActivePotionEffects();
                for (PotionEffect effect : pe) {
                    if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                        if (effect.getAmplifier() == 0) {
                            damage = damage / 0.80;
                            damage = damage * 0.90;
                        } else if (effect.getAmplifier() == 1) {
                            damage = damage / 0.60;
                            damage = damage * 0.85;
                        } else if (effect.getAmplifier() == 2) {
                            damage = damage / 0.40;
                            damage = damage * 0.80;
                        } else if (effect.getAmplifier() == 3) {
                            damage = damage / 0.20;
                            damage = damage * 0.75;
                        }
                    }
                }
            }
        }

        event.setDamage(damage);
    }
}
