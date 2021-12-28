package com.tharox.dragonhunt.listeners;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " has left the Server.");

        if (Bukkit.getOnlinePlayers().size() == 1) {
            DbValues.updateServerStatus();
            Bukkit.getServer().shutdown();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(ChatColor.RED.toString() + Bukkit.getOnlinePlayers().size() + " players remaining.");
        }
    }
}
