package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.modifiers.classes.PoisonousWater;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMoving(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getLocation().clone().add(0, -1, 0).getBlock().getType() == Material.WATER) {
            if (PoisonousWater.active) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 0));
            }
        }
    }
}
