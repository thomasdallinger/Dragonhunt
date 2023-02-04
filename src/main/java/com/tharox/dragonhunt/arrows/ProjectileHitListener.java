package com.tharox.dragonhunt.arrows;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener {

    /**
     * Arrows disappear when hitting an entity / the ground.
     */
    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        event.getEntity().remove();
    }
}
