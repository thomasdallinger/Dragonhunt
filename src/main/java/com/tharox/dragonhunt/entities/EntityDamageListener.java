package com.tharox.dragonhunt.entities;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.grappler.GrappleListener;
import com.tharox.dragonhunt.kits.classes.Grappler;
import com.tharox.dragonhunt.modifiers.classes.FireWeakness;
import com.tharox.dragonhunt.modifiers.classes.Frozen;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class EntityDamageListener implements Listener {

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

            // put the GRAPPLER on cooldown
            player.setCooldown(Material.CROSSBOW,
                    Grappler.grapplerCooldown.getOrDefault(player, GrappleListener.cooldown));

            // after the cooldown ends, add the next charged projectile
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                CrossbowMeta crossbowMeta = (CrossbowMeta) itemInMainHand.getItemMeta();
                crossbowMeta.addChargedProjectile(new ItemStack(Material.ARROW, 1));
                itemInMainHand.setItemMeta(crossbowMeta);

                Grappler.grapplerCooldown.getOrDefault(player, GrappleListener.cooldown);
            }, 5L);
        }

        // if a living entity is on fire and the modifier is active, let it glow and give weakness to the entity
        if (FireWeakness.active) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity.getFireTicks() > 20) {
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                }
            }
        }

        // if a living entity is blinded and the modifier is active, freeze it
        if (Frozen.active) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                    if (livingEntity.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                        livingEntity.setFreezeTicks(livingEntity.getMaxFreezeTicks());
                    }
                }, 5L);
            }
        }
    }
}
