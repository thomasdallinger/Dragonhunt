package com.tharox.dragonhunt.players;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    public static boolean gracePeriodActive = false;

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            if (gracePeriodActive) {
                // Players cannot receive ANY damage.
                event.setCancelled(true);
            }
        }
    }
}
