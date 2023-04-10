package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameEnd;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PointsListener implements Listener {
    public static int threshold = 300;

    // contains the UUID of players who are forced to damage the Ender Dragon
    public static HashMap<UUID, Integer> scorePerPlayer = new HashMap<>();
    public static HashMap<UUID, BossBar> bossBars = new HashMap<>();

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
            if (damage > 0) {
                scorePerPlayer.put(id, scorePerPlayer.get(id) + damage);
            }

            // Ender Dragon never looses health.
            EnderDragon dragon = (EnderDragon) target;
            double maxHealth = Objects.requireNonNull(
                    dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            dragon.setHealth(maxHealth);
        } else {
            scorePerPlayer.put(id, scorePerPlayer.get(id) + 5);
        }

        // Player has won
        if (scorePerPlayer.get(id) >= threshold) {
            scorePerPlayer.put(id, threshold); // points cannot be above the threshold
            bossBars.get(id).setTitle(HexColor.YELLOW.toString() + ChatColor.BOLD
                    + threshold + ChatColor.WHITE + " / " + threshold + " Points");
            bossBars.get(id).setProgress(1);
            new GameEnd();
            return;
        }

        bossBars.get(id).setTitle(HexColor.YELLOW.toString() + ChatColor.BOLD
                + scorePerPlayer.get(id) + ChatColor.WHITE + " / " + threshold + " Points");
        bossBars.get(id).setProgress(scorePerPlayer.get(id) / (threshold + 1D));

        if ((threshold - scorePerPlayer.get(id)) <= 25) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Prefixes.getImportantPrefix() + ChatColor.WHITE + player.getName()
                        + ChatColor.GRAY + " needs "
                        + HexColor.YELLOW + (threshold - scorePerPlayer.get(id))
                        + ChatColor.GRAY + " more points to win.");
            }
        }
    }
}
