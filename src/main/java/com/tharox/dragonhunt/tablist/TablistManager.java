package com.tharox.dragonhunt.tablist;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TablistManager {

    /**
     * Sets a custom header and footer in a players tablist.
     *
     * @param player player who receives the header and footer in the tablist
     */
    public void setPlayerList(Player player) {
        player.setPlayerListHeaderFooter(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD
                + "DRAGON" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "HUNT", "v.0.4 (Alpha)");
    }

    /**
     * Executed every second in Timer to keep stats up to date.
     */
    public void setAllPlayerTeams() {
        Bukkit.getOnlinePlayers().forEach(this::setPlayerTeams);
    }

    /**
     * One player receives the formatted tablist (using teams) which includes
     * certain stats of all online players.
     * Every player has his own scoreboard team.
     *
     * @param player player who receives the formatted tablist
     */
    public void setPlayerTeams(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        int playerCount = 0;

        for (Player target : Bukkit.getOnlinePlayers()) { // sorted alphabetically
            Team team = scoreboard.getTeam(playerCount + "player");
            if (team == null)
                team = scoreboard.registerNewTeam(playerCount + "player");

            team.setColor(ChatColor.YELLOW);
            team.setPrefix(Main.getNamePrefix(target));
            team.addEntry(target.getName());

            playerCount++;
        }
    }
}
