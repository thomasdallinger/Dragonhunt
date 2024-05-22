package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.shop.Enchantments;
import com.tharox.dragonhunt.shop.Upgrades;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

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
                    damager.playSound(damager.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    damager.playSound(damager.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 1.0f);
                }
            }
        }

        event.setDamage(damage);
    }
}
