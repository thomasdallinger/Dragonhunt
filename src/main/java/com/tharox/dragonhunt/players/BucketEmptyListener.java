package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class BucketEmptyListener implements Listener {
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();
        Player player = event.getPlayer();

        // prevent players from placing lava close to the NETHER portal inside the NETHER
        if (GameState.firstNetherEntry != null && player.getWorld().getName().equals("world_nether")) {
            if ((int) player.getLocation().distance(GameState.firstNetherEntry) < 15
                    && bucket.toString().contains("LAVA")) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You are not allowed to place "
                        + HexColor.RED + "Lava" + ChatColor.GRAY + " close to this Nether Portal.");
            }
        }

        // prevent players from placing lava close to the END spawn platform
        if (player.getWorld().getName().equals("world_the_end")) {
            if ((int) player.getLocation().distance(GameState.endPlatform) < 15
                    && bucket.toString().contains("LAVA")) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You are not allowed to place "
                        + HexColor.RED + "Lava" + ChatColor.GRAY + " near the End Spawn Platform.");
            }
        }
    }
}
