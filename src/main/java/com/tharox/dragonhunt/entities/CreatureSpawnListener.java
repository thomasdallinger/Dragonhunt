package com.tharox.dragonhunt.entities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        /*if (event.getEntity() instanceof Ghast) {
            event.setCancelled(true);
        }*/
    }
}
