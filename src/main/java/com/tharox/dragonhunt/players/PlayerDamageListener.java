package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.kits.classes.KitClass;
import com.tharox.dragonhunt.modifiers.classes.BadOmen;
import com.tharox.dragonhunt.modifiers.classes.HotHealing;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlayerDamageListener implements Listener {

    public static boolean gracePeriodActive = false;

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Player player;

        if (entity instanceof Player) {
            player = (Player) entity;

            if (gracePeriodActive) {
                // Players cannot receive ANY damage.
                event.setCancelled(true);
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (KitClass.flankers.contains(player)) {
                    event.setCancelled(true);
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                if (HotHealing.active) {
                    event.setCancelled(true);

                    // Only add health if the player is below max health
                    if (player.getHealth() < Objects.requireNonNull(
                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()) {
                        player.setHealth(player.getHealth() + 1);
                    }
                }
            }

            // BadOmen Modifier
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (BadOmen.active && player.getWorld().getEnvironment() == World.Environment.NETHER) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
                }
            }
        }
    }
}
