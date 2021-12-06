package com.tharox.dragonhunt.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setFormat(ChatColor.YELLOW + "%1$s" + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + "%2$s");
    }
}
