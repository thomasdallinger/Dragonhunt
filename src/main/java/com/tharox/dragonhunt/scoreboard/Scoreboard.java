package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.points.Points;
import com.tharox.dragonhunt.timer.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Scoreboard extends ScoreboardBuilder {

    private int socialId;

    public Scoreboard(Player player) {
        super(player, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Dragon" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Hunt");
        socialId = 0;

        run(player, Main.getInstance().getScoreboardCountdown());
    }

    @Override
    public void createScoreboard() {
        setScore(ChatColor.DARK_GRAY + "---------------", 11);
        setScore(ChatColor.WHITE + "Starting soon!", 10);
        setScore("", 9);
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
    public void update() {

    }

    private void run(Player player, Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (!Main.getInstance().getTimer().isRunning()) { //Game hasn't started yet
                    gameNotStarted();
                    return;
                }

                setScore(ChatColor.GOLD.toString() + ChatColor.BOLD + "1vs1", 10);
                setScore(ChatColor.WHITE + "Players: " + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size(), 8);
                setScore(ChatColor.WHITE + "Kills: " + ChatColor.YELLOW + "", 6);
                setScore(ChatColor.WHITE + "Deaths: " + ChatColor.RED + "", 5);
                setScore(ChatColor.WHITE + "Points: " + ChatColor.AQUA.toString() + Points.getPoints(player), 3);
                setScore(ChatColor.WHITE + "Next Points in:", 1);
                setScore(ChatColor.YELLOW + countdownFormat(countdown), 0);
                //TODO: use db for kills, deaths

            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    /**
     * Executed when the game hasn't started (timer is not running)
     */
    private void gameNotStarted() {
        setScore(ChatColor.WHITE + "Players: " + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size(), 8);
        switch (socialId) {
            case 0:
                setScore(ChatColor.YELLOW + "Waiting.", 6);
                break;
            case 1:
                setScore(ChatColor.YELLOW + "Waiting..", 6);
                break;
            case 2:
                setScore(ChatColor.YELLOW + "Waiting...", 6);
                break;
        }

        socialId++;

        if (socialId >= 3) {
            socialId = 0;
        }
    }

    private String countdownFormat(Countdown countdown) {

        String minutes = "";
        String seconds = "";
        if ((countdown.getTime() / 60) % 60 < 10)
            minutes = "0";
        minutes += (countdown.getTime() / 60) % 60;
        if (countdown.getTime() % 60 < 10)
            seconds = "0";
        seconds += countdown.getTime() % 60;

        return minutes + ":" + seconds;
    }
}
