package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.UUID;

public class EnderDragonDamageListener implements Listener {
    public static int time = 40;

    // contains the UUID of players who are forced to damage the Ender Dragon
    public static HashMap<UUID, Integer> timeLeft = new HashMap<>();

    public static BossBar bossBar = Bukkit.createBossBar(HexColor.RED + "Damage the Ender Dragon or you die!",
            BarColor.RED, BarStyle.SEGMENTED_10);

    public EnderDragonDamageListener() {
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
     * Target: Ender Dragon
     * Damager: Player
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        AbstractArrow arrow;
        ProjectileSource shooter;
        Player player;

        if (!(target instanceof EnderDragon)) return;

        if (damager instanceof AbstractArrow) {
            arrow = (AbstractArrow) damager;
            shooter = arrow.getShooter();

            if (shooter instanceof Player) {
                player = (Player) shooter;
            } else return;

        } else if (damager instanceof Player) {
            player = (Player) damager;
        } else return;

        timeLeft.put(player.getUniqueId(), time);

        if (!bossBar.getPlayers().contains(player))
            bossBar.addPlayer(player);
    }

    public void onInterval() {
        HashMap<UUID, Integer> temp = new HashMap<>();

        try {
            for (UUID id : timeLeft.keySet()) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) continue;

                if (player.isDead()) {
                    temp.remove(id);
                    bossBar.removePlayer(player);
                    continue;
                }

                int timer = timeLeft.get(id) - 1;

                if (timer > 0) {
                    temp.put(id, timer);
                    bossBar.setProgress(timer / (time + 1D));
                } else {
                    temp.remove(id);
                    bossBar.removePlayer(player);

                    player.setHealth(0);
                    player.sendMessage(Prefixes.getDeathPrefix()
                            + ChatColor.RED + "You did not damage the Ender Dragon in time.");
                    player.sendMessage(Prefixes.getDeathPrefix()
                            + ChatColor.RED + "The Ender Dragon killed you.");
                }

                bossBar.setVisible(true);
            }
        } catch (ConcurrentModificationException exception) {
            // do nothing
        }

        timeLeft = temp;
    }
}
