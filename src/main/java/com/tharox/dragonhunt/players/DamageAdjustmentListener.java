package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.kits.classes.KitClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

/**
 * The first damage assignment resets the vanilla values.
 */
public class DamageAdjustmentListener implements Listener {
    public static double assassinDamageBuff = 1.25;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity edamager = event.getDamager();
        double damage = event.getDamage();

        // Nerf strength.
        if (edamager instanceof Player) {
            Player damager = (Player) edamager;

            if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                Collection<PotionEffect> pe = damager.getActivePotionEffects();
                for (PotionEffect effect : pe) {
                    if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        damage = damage - effect.getAmplifier() * 1.5; // Remove default damage increase.
                        damage = damage + effect.getAmplifier();
                    }
                }
            }

            // Assassins passive: Buff all damage by 25%
            if (KitClass.assassins.contains(damager)) {
                damage = damage * assassinDamageBuff;
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
                            damage = damage * 0.80;
                        } else if (effect.getAmplifier() == 2) {
                            damage = damage / 0.40;
                            damage = damage * 0.70;
                        }
                    }
                }
            }
        }

        event.setDamage(damage);
    }
}
