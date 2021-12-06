package com.tharox.dragonhunt.listeners;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameStart;
import com.tharox.dragonhunt.scoreboard.Scoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Welcome to DragonHunt!");
        event.setJoinMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " has joined the Server.");

        new Scoreboard(player);
        if(player.getName().equals("TheBuilder_LP")) new GameStart();
    }

}
