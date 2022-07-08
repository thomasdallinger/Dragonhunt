package com.tharox.dragonhunt.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameStart;
import com.tharox.dragonhunt.points.UpgradePoints;
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

        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Welcome to Dragonhunt!");
        event.setJoinMessage(Main.getPrefix() + ChatColor.GREEN + player.getName() + ChatColor.WHITE + " has joined.");

        UpgradePoints.setPoints(player, 0);
        new Scoreboard(player);
        Main.getInstance().getTablistManager().setPlayerList(player);

        DbValues.getPlayersAllowedToPlay();
        checkIfGameCanStart();
    }

    private void checkIfGameCanStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // if a player joining is not allowed to play, send the player back to the lobby
            if (!DbValues.playersAllowedToPlay.contains(player.getName())) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("lobby");

                player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
                return;
            }

            // if the player is allowed to play, remove the player from the list
            DbValues.playersAllowedToPlay.remove(player.getName());

            // if there are no more playerNames in the list, start the game
            if (DbValues.playersAllowedToPlay.size() == 0) {
                new GameStart();
                return;
            }
        }

        // if there are more players in the playersAllowedToPlay list, send a message to all players on the server
        // telling them how many more players are needed
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(ChatColor.RED + "Need " + ChatColor.DARK_RED + ChatColor.BOLD
                    + DbValues.playersAllowedToPlay.size() + " more players to start.");
        }
    }
}
