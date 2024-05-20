package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.classes.FlyHigh;
import com.tharox.dragonhunt.modifiers.classes.WeakenedByFlames;
import com.tharox.dragonhunt.modifiers.classes.Frozen;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class ArrowDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        AbstractArrow arrow; // includes normal Arrows, Tipped Arrows and Spectral Arrows
        ProjectileSource shooter;
        Player player;
        ItemStack itemInMainHand; // Grappler can never be in a players Offhand

        if (damager instanceof AbstractArrow) {
            arrow = (AbstractArrow) damager;
            shooter = arrow.getShooter();
        } else return;

        if (shooter instanceof Player) {
            player = (Player) shooter;
        } else return;

        // Grappler can't do damage
        itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() == null) return;
        if (itemInMainHand.getItemMeta().getDisplayName().contains("Grappler")) {
            arrow.remove();
            event.setCancelled(true);
        }

        // If a living entity is shot with flame and the modifier is active give weakness to the entity.
        if (WeakenedByFlames.active) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity.getFireTicks() > 20) {
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 12 * 20, 2));
                }
            }
        }

        // If a living entity is shot with an effect arrow (except levitation) and the modifier is active, freeze it.
        if (Frozen.active) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                    if (livingEntity.hasPotionEffect(PotionEffectType.BLINDNESS)
                            || livingEntity.hasPotionEffect(PotionEffectType.WEAKNESS)
                            || livingEntity.hasPotionEffect(PotionEffectType.SLOW_DIGGING)
                            || livingEntity.hasPotionEffect(PotionEffectType.POISON)) {
                        livingEntity.setFreezeTicks(livingEntity.getMaxFreezeTicks());
                    }
                }, 5L);
            }
        }
    }
}
