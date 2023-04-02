package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameEnd;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PointsListener implements Listener {
    public static int threshold = 100;

    // contains the UUID of players who are forced to damage the Ender Dragon
    public static HashMap<UUID, Integer> scorePerPlayer = new HashMap<>();
    public static HashMap<UUID, BossBar> bossBars = new HashMap<>();

    public static BossBar bossBar = Bukkit.createBossBar(ChatColor.AQUA + "0 / " + threshold + " Points",
            BarColor.BLUE, BarStyle.SEGMENTED_10);

    /**
     * Target: Ender Dragon, Ender Crystal
     * Damager: Player
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        int damage = (int) event.getDamage();
        AbstractArrow arrow;
        ProjectileSource shooter;
        Player player;

        if (!(target instanceof EnderDragon) && !(target instanceof EnderCrystal)) return;

        if (damager instanceof AbstractArrow) {
            arrow = (AbstractArrow) damager;
            shooter = arrow.getShooter();

            if (shooter instanceof Player) {
                player = (Player) shooter;
            } else return;

        } else if (damager instanceof Player) {
            player = (Player) damager;
        } else return;

        UUID id = player.getUniqueId();

        if (target instanceof EnderDragon) {
            scorePerPlayer.put(id, scorePerPlayer.get(id) + damage);

            // Ender Dragon never looses health.
            EnderDragon dragon = ((EnderDragon) target);
            double maxHealth = Objects.requireNonNull(
                    dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            dragon.setHealth(maxHealth);
        } else {
            scorePerPlayer.put(id, scorePerPlayer.get(id) + 10);
        }

        if (scorePerPlayer.get(id) >= threshold) {
            new GameEnd(player);
            return;
        }

        bossBars.get(id).setTitle(ChatColor.AQUA.toString()
                + scorePerPlayer.get(id) + " / " + threshold + " Points");
        bossBars.get(id).setProgress(scorePerPlayer.get(id) / (threshold + 1D));

        if ((threshold - scorePerPlayer.get(id)) <= 30) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Prefixes.getImportantPrefix() + ChatColor.WHITE + player.getName()
                        + ChatColor.AQUA + " needs "
                        + ChatColor.DARK_AQUA + (threshold - scorePerPlayer.get(id))
                        + ChatColor.AQUA + " more points to win.");
            }
        }
    }
}
