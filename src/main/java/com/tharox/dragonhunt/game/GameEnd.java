package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GameEnd {

    Player winner;
    Timer timer = Main.getInstance().getTimer();

    /**
     * no winner - time ran out
     */
    public GameEnd() {
        timer.setRunning(false);

        setGamemodeToSpectator();
    }

    public GameEnd(Player winner) {
        this.winner = winner;
        timer.setRunning(false);

        setGamemodeToSpectator();
    }

    private void setGamemodeToSpectator() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Main.getPrefix() + ChatColor.GOLD + "The game is over!");

            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);

            if(player == winner) {
                player.sendTitle(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "YOU WON", ChatColor.GREEN + "You defeated the Enderdragon", 10, 90, 20);
            } else {
                player.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "YOU LOST", ChatColor.RED + "Try again next time", 10, 90, 20);
            }

            //TODO: Time played message
            //TODO: Game Stats messages?
        }
    }
}
