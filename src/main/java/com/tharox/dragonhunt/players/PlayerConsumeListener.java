package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeListener implements Listener {
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if ((player.getWorld().getName().equals("world_nether") && event.getItem().getType() == Material.POTION) &&
                !GameState.minOnePlayerInEnd) {
            event.setCancelled(true);
        }
    }
}
