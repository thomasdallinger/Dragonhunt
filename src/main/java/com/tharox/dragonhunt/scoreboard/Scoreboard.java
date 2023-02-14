package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.entities.EntityDropListener;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.PlayerEndTeleport;
import com.tharox.dragonhunt.timer.Countdown;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Scoreboard extends ScoreboardBuilder {
    Timer timer = Main.getInstance().getTimer();

    public Scoreboard(Player player) {
        super(player, Main.getTitle());

        run(player, Main.getInstance().getScoreboardCountdown());
    }

    @Override
    public void createScoreboard() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();

        setScore(ChatColor.GRAY + dtf.format(now) + ChatColor.DARK_GRAY + " ["
                + DbValues.getServerID() + "]", 10);
        setScore(ChatColor.WHITE + "12x1" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Casual", 9);
        setScore("", 8);
        setScore("", 7);
        setScore("", 6);
        setScore("", 5);
        setScore("", 4);
        setScore("", 3);
        setScore("", 2);
        setScore("", 1);
        setScore("", 0);
    }

    @Override
    public void update(Player player, Countdown countdown) {
        if (GameState.selectedKit.containsKey(player)) {
            setScore(GameState.selectedKit.get(player), 7);
        }

        setScore("☻ " + Bukkit.getOnlinePlayers().size() + " "
                + "☠ " + DbValues.getKills(player, true) + " "
                + "✟ " + DbValues.getKills(player, false), 6);

        if (timer.getTime() <= 1500) {
            setScore(ChatColor.WHITE + "✮ " + ChatColor.DARK_PURPLE + PlayerEndTeleport.getEyesNeeded()
                    + ChatColor.DARK_GRAY + " » "
                    + ChatColor.WHITE + countdownFormat(1500 - timer.getTime()), 4);
        } else {
            setScore(ChatColor.WHITE + "✮ " + ChatColor.DARK_PURPLE + PlayerEndTeleport.getEyesNeeded(),
                    3);
        }

        setScore(ChatColor.WHITE + "⛃ " + HexColor.HIGHLIGHT + Coins.getCoins(player)
                + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + countdownFormat(countdown.getTime()), 3);

        if (player.getWorld().getName().equals("world_nether")) {
            setScore(ChatColor.WHITE + "♦ " + ChatColor.DARK_GRAY + EntityDropListener.nextDrop, 2);
        } else {
            setScore("", 2);
        }

        setScore(ChatColor.GOLD + "ᴅʀᴀɢᴏɴʜᴜɴᴛ.ɴᴇᴛ", 0);
    }

    private void run(Player player, Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {
                update(player, countdown);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private String countdownFormat(int time) {

        String minutes = "";
        String seconds = "";
        if ((time / 60) % 60 < 10)
            minutes = "0";
        minutes += (time / 60) % 60;
        if (time % 60 < 10)
            seconds = "0";
        seconds += time % 60;

        return minutes + ":" + seconds;
    }
}
