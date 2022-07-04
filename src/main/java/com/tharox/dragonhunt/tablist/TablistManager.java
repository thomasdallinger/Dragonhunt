package com.tharox.dragonhunt.tablist;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TablistManager {

    public void setPlayerList(Player player) {
        player.setPlayerListHeaderFooter(Main.getPrefix(), "Alpha 3.0");
    }

    /**
     * Executed every second in Timer to keep stats up to date.
     */
    public void setAllPlayerTeams() {
        Bukkit.getOnlinePlayers().forEach(this::setPlayerTeams);
    }


    public void setPlayerTeams(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        Team players = scoreboard.getTeam("");

        if (players == null) {
            players = scoreboard.registerNewTeam("");
        }

        players.setPrefix(Main.getNamePrefix(player));
        players.setColor(ChatColor.YELLOW);

        for (Player target : Bukkit.getOnlinePlayers()) {
            players.addEntry(target.getName());
        }
    }
}
