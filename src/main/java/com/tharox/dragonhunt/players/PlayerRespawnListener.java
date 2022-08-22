package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
            Player player = event.getPlayer();

            if (GameState.itemsToKeepOnDeath.containsKey(player) && GameState.itemsToKeepOnDeath.get(player) != null) {
                player.getInventory().setContents(GameState.itemsToKeepOnDeath.get(player));

                player.sendMessage(Prefixes.getDeathPrefix() + ChatColor.GRAY + "Respawned with "
                        + ChatColor.YELLOW + "Hotbar, Armor" + ChatColor.GRAY + " and " + ChatColor.YELLOW + "Offhand"
                        + ChatColor.GRAY + " items.");
            }
        }, 5L);
    }
}
