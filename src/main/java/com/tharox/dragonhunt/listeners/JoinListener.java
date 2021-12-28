package com.tharox.dragonhunt.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameStart;
import com.tharox.dragonhunt.points.Points;
import com.tharox.dragonhunt.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.teleport(new Location(Bukkit.getServer().getWorld("world"), 0, 15000, 0));
        player.setGameMode(GameMode.SPECTATOR);

        player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Welcome to DragonHunt!");
        event.setJoinMessage(Main.getPrefix() + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " has joined the Server.");

        Points.setPoints(player, 0);
        new Scoreboard(player);
        checkIfPlayerAllowedToPlay(player);
    }

    private void checkIfPlayerAllowedToPlay(Player player) {
        if(!DbValues.playersAllowedToPlay.contains(player.getName())) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("lobby");

            player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
            return;
        }

        DbValues.playersAllowedToPlay.remove(player.getName());

        if(DbValues.playersAllowedToPlay.size() == 0) {
            new GameStart();
        }
    }
}
