package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.Prefixes;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class PlayerBedEnterListener implements Listener {

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Prefixes.getErrorPrefix() + "Sleeping is not allowed.");
        event.setCancelled(true);
    }
}
