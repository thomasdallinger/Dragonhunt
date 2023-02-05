package com.tharox.dragonhunt.timer;

import com.tharox.dragonhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends Timer {

    private TimerExecutor timerExecutor;

    public Countdown(boolean running, int time) {
        super(running, time);
    }

    public Countdown(boolean running, int time, TimerExecutor timerExecutor) {
        super(running, time);
        this.timerExecutor = timerExecutor;
    }

    /**
     * Countdown stops once the time hits 0 (after executing the timerExecutor one last time) - plays sound to all players
     */
    @Override
    protected void run() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (!isRunning()) {
                    return;
                }

                if (timerExecutor != null) {
                    timerExecutor.execute(getTime());
                }

                if (getTime() == 0) {
                    setRunning(false);

                    for (Player player : Bukkit.getOnlinePlayers())
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage("Countdown: " + getTime());
                }

                setTime(getTime() - 1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20); //20 Ticks = 1 Second
    }
}
