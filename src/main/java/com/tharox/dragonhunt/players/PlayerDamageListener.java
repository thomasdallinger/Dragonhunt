package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.modifiers.classes.BadOmen;
import com.tharox.dragonhunt.modifiers.classes.HotHealing;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

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

            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                if (HotHealing.active) {
                    event.setCancelled(true);
                    player.setHealth(player.getHealth() + 1);
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                Random random = new Random();
                int chance = random.nextInt(5) + 1;

                // 20% chance to get the Wither Effect.
                if (BadOmen.active && player.getWorld().getEnvironment() == World.Environment.NETHER && chance == 1) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
                }
            }
        }
    }
}
