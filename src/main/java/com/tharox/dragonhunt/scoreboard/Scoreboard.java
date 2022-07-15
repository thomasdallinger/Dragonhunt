package com.tharox.dragonhunt.scoreboard;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.points.UpgradePoints;
import com.tharox.dragonhunt.spectate.Spectate;
import com.tharox.dragonhunt.timer.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class Scoreboard extends ScoreboardBuilder {

    private int socialId;
    private ArrayList<String> funMessages = new ArrayList<>();
    private String message = "";

    public Scoreboard(Player player) {
        super(player, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "DRAGON" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "HUNT");
        socialId = 0;

        funMessages.add("Repairing Grappler");
        funMessages.add("Locating Player Skill");
        funMessages.add("Mining Dirt");
        funMessages.add("Respawning Zombies");
        funMessages.add("Eating Apples");
        funMessages.add("Creating Memes");
        funMessages.add("Blowing up worlds");
        funMessages.add("Loosing to Creepers");
        funMessages.add("Dragon not Perching");
        funMessages.add("Calculating Skill Level");
        funMessages.add("Dying to Piglin Brutes");

        run(player, Main.getInstance().getScoreboardCountdown());
    }

    @Override
    public void createScoreboard() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();

        setScore(ChatColor.GRAY + dtf.format(now) + ChatColor.DARK_GRAY + " ["
                + DbValues.getServerID() + "]", 11);
        setScore(ChatColor.GOLD.toString() + ChatColor.BOLD + "1vs1", 10);
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
    public void update(Player player, Countdown countdown) {
        if (!Main.getInstance().getTimer().isRunning()
                && UpgradePoints.getPoints(player) == 0) { //Game hasn't started yet
            gameNotStarted();
            return;
        }

        setScore(ChatColor.WHITE + "Players: "
                + ChatColor.DARK_GREEN + Bukkit.getOnlinePlayers().size(), 8);
        setScore(ChatColor.WHITE + "Kills: "
                + ChatColor.GREEN + DbValues.getKills(player, true), 7);
        setScore(ChatColor.WHITE + "Deaths: "
                + ChatColor.GREEN + DbValues.getKills(player, false), 6);
        setScore(ChatColor.WHITE + "Spectate Tokens: "
                + ChatColor.GOLD.toString() + Spectate.spectatesLeft.get(player), 4);
        setScore(ChatColor.WHITE + "Upgrade Points: "
                + ChatColor.LIGHT_PURPLE.toString() + UpgradePoints.getPoints(player), 3);
        setScore(ChatColor.WHITE + "Free XP in:", 1);
        setScore(ChatColor.AQUA + countdownFormat(countdown), 0);
    }

    private void run(Player player, Countdown countdown) {
        new BukkitRunnable() {
            @Override
            public void run() {
                update(player, countdown);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    /**
     * Executed when the game hasn't started (timer not running, players have 0 points)
     */
    private void gameNotStarted() {
        if (socialId == 0) {
            Random rand = new Random();
            int random = rand.nextInt(10); // generates random value from 0-9
            message = funMessages.get(random);
        }

        setScore(ChatColor.WHITE + "Players: " + ChatColor.DARK_GREEN
                + Bukkit.getOnlinePlayers().size(), 8);
        setScore("Starting soon!", 6);
        // a lot of places to make the scoreboard wider - scoreboard size won't change with every new random message
        // will be replaced once the game starts
        setScore("                                ", 4);

        switch (socialId) {
            case 0:
                setScore(ChatColor.YELLOW.toString() + ChatColor.ITALIC + message + ".", 3);
                break;
            case 1:
                setScore(ChatColor.YELLOW.toString() + ChatColor.ITALIC + message + "..", 3);
                break;
            case 2:
                setScore(ChatColor.YELLOW.toString() + ChatColor.ITALIC + message + "...", 3);
                break;
            case 3:
                setScore(ChatColor.YELLOW.toString() + ChatColor.ITALIC + message + "....", 3);
                break;
        }

        // Skill Level: implement average rank in this round (from DB)

        socialId++;

        if (socialId >= 4) {
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
