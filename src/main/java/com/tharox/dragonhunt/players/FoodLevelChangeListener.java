package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.modifiers.classes.NeverHungry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (NeverHungry.active) {
            event.setCancelled(true);
        }
    }
}
