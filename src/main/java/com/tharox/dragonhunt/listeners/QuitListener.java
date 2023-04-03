package com.tharox.dragonhunt.listeners;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(Prefixes.getLeavePrefix() + ChatColor.WHITE + player.getName()
                + ChatColor.GRAY + " has left.");

        if (Bukkit.getOnlinePlayers().size() == 1) {
            DbValues.updateServerStatus();
            Bukkit.getServer().shutdown();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Prefixes.getLeavePrefix() + HexColor.RED
                    + (Bukkit.getOnlinePlayers().size() - 1) + ChatColor.GRAY + " player(s) remaining.");
            // - 1 because the player that left is still included in the OnlinePlayers list at that time
        }
    }
}
