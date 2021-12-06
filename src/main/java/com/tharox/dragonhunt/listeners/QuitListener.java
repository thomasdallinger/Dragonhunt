package com.tharox.dragonhunt.listeners;

import com.tharox.dragonhunt.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " has left the Server.");

    }

}
