package com.tharox.dragonhunt.playerlisteners;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * Don't display the advancement for finding a fortress or a bastion when walking through a nether dungeon.
 */
public class AdvancementListener implements Listener {
    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement adv = event.getAdvancement();

        if (adv.getKey().toString().equals("minecraft:story/find_fortress") || adv.getKey().toString().equals("minecraft:story/find_bastion")) {
            for(String c: adv.getCriteria()) {
                player.getAdvancementProgress(adv).revokeCriteria(c);
            }
        }
    }
}
