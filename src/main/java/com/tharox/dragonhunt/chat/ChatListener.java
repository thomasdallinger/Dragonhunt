package com.tharox.dragonhunt.chat;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setFormat(Prefixes.getNamePrefix(event.getPlayer())
                + ChatColor.WHITE + "%1$s" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "%2$s");
    }
}
