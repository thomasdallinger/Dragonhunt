package com.tharox.dragonhunt.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameStart;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.scoreboard.Scoreboard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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

        if (GameState.gameID == 0) {
            GameState.gameID = DbValues.getGameID();
        }

        event.setJoinMessage(null);
        player.teleport(new Location(Bukkit.getServer().getWorld("world"), 0, 15000, 0));
        player.setGameMode(GameMode.SPECTATOR);

        new Scoreboard(player);
        Main.getInstance().getTablistManager().setPlayerList(player); // sets tablist header and footer

        if (!Main.firstPlayerJoined) { // only false if no player has joined the server yet
            Main.firstPlayerJoined = true;
        }

        DbValues.getPlayersAllowedToPlay();
        checkIfGameCanStart();
    }

    private void checkIfGameCanStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // if a player joining is not allowed to play
            // or is trying to rejoin after the game already started,
            // send the player back to the lobby
            if (!DbValues.playersAllowedToPlay.contains(player.getUniqueId().toString())
                    || Main.getInstance().getTimer().isRunning()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("lobby");

                player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
                return;
            }

            // if the player is allowed to play, remove the player from the list
            DbValues.playersAllowedToPlay.remove(player.getUniqueId().toString());

            // if there are no more playerNames in the list, start the game
            if (DbValues.playersAllowedToPlay.size() == 0) {
                new GameStart();
                return;
            }
        }

        // if there are more players in the playersAllowedToPlay list, send a message to all players on the server
        // telling them how many more players are needed
        // start the "stopServerIn" runnable
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(ChatColor.GRAY + "Waiting for " + HexColor.YELLOW + DbValues.playersAllowedToPlay.size()
                    + ChatColor.GRAY + " more player(s).");
        }
    }
}
