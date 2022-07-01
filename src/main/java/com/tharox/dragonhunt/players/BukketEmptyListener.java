package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class BukketEmptyListener implements Listener {
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();
        Player player = event.getPlayer();

        // prevent players from placing lava close to the NETHER portal inside the NETHER
        if (GameState.firstNetherEntry != null && player.getWorld().getName().equals("world_nether")) {
            if ((int) player.getLocation().distance(GameState.firstNetherEntry) < 15
                    && bucket.toString().contains("LAVA")) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.RED + "You are not allowed to place lava close to this nether portal!");
            }
        }

        // prevent players from placing lava close to the END spawn platform
        if (player.getWorld().getName().equals("world_the_end")) {
            if ((int) player.getLocation().distance(GameState.endPlatform) < 15
                    && bucket.toString().contains("LAVA")) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.RED + "You are not allowed to place lava near the spawn platform!");
            }
        }
    }
}
