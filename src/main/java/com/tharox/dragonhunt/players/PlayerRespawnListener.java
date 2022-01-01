package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.points.Points;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
            Player player = event.getPlayer();

            player.getInventory().setContents(GameState.itemsToKeepOnDeath.get(player));

            player.sendMessage(ChatColor.GOLD + "You respawned with your Hotbar, Offhand and Armor Items.");
            player.sendMessage(ChatColor.RED + "You now have " + ChatColor.DARK_RED + Points.getPoints(player)
                    + ChatColor.RED + " Points left.");

        }, 5L);
    }
}
