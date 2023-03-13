package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDamagesEnderdragon implements Listener {
    public static int time = 18;

    // contains the UUID of players who are forced to damage the Enderdragon
    public static HashMap<UUID, Integer> timeLeft = new HashMap<>();

    static BossBar bossBar = Bukkit.createBossBar(HexColor.RED + "Damage the Enderdragon or you die!",
            BarColor.RED, BarStyle.SEGMENTED_6);

    public PlayerDamagesEnderdragon() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.getInstance().getTimer().isRunning()) {
                    onInterval();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    /**
     * Target: Enderdragon
     * Damager: Player
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderDragon && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            timeLeft.put(damager.getUniqueId(), time);

            if (!bossBar.getPlayers().contains(damager))
                bossBar.addPlayer(damager);
        }
    }

    public void onInterval() {
        HashMap<UUID, Integer> temp = new HashMap<>();

        try {
            for (UUID id : timeLeft.keySet()) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) return;
                int timer = timeLeft.get(id) - 1;

                if (timer > 0) {
                    temp.put(id, timer);
                    bossBar.setProgress(timer / (time + 1D));
                } else {
                    temp.remove(id);
                    bossBar.removePlayer(player);

                    player.setHealth(0);
                    player.sendMessage(Prefixes.getErrorPrefix() + "You did not damage the Enderdragon in time.");
                    player.sendMessage(Prefixes.getErrorPrefix() + "The Enderdragon killed you.");
                }

                bossBar.setVisible(true);
            }
        } catch (ConcurrentModificationException exception) {
            // do nothing
        }

        timeLeft = temp;
    }
}
