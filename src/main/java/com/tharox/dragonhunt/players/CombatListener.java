package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Statement;
import java.util.*;

public class CombatListener implements Listener {
    // contains the UUID of players who are in combat
    public static HashMap<UUID, Integer> combatList = new HashMap<>();

    public CombatListener() {
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
     * For players only.
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Projectile projectile; // includes all types of arrows and fireworks
        ProjectileSource shooter;
        Player damager;
        Player target;

        if (event.getEntity() instanceof Player)
            target = (Player) event.getEntity();
        else {
            return;
        }

        if (event.getDamager() instanceof Projectile) {
            projectile = (Projectile) event.getDamager();
            shooter = projectile.getShooter();

            if (shooter instanceof Player) {
                damager = (Player) shooter;
            } else return;
        } else if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else return;

        // Once the grace period has ended, the in combat feature comes into effect.
        if (Main.getInstance().getTimer().getTime() < GameState.gracePeriodTime) return;

        // If the player damaged himself/herself, do nothing.
        if(target == damager) return;

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO in_combat (game_id, target, damager, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + target.getUniqueId() + "', '"
                    + damager.getUniqueId() + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        combatList.put(target.getUniqueId(), 30);
        combatList.put(damager.getUniqueId(), 30);
    }

    public void onInterval() {
        HashMap<UUID, Integer> temp = new HashMap<>();

        try {
            for (UUID id : combatList.keySet()) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) continue;

                if (player.isDead()) {
                    temp.remove(id);
                    continue;
                }

                int timer = combatList.get(id) - 1;

                if (timer > 0) {
                    temp.put(id, timer);
                } else {
                    temp.remove(id);

                    player.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + "You are no longer in combat.");
                }
            }
        } catch (ConcurrentModificationException exception) {
            // do nothing
        }

        combatList = temp;
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {
        if (combatList.containsKey(event.getPlayer().getUniqueId())) {
            List<String> commands = Arrays.asList("/date", "/scout", "/top", "/hub", "/l", "/lobby");

            String[] parts = event.getMessage().split(" ");
            String cmd = parts[0].toLowerCase();

            if (commands.contains(cmd)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Prefixes.getErrorPrefix()
                        + ChatColor.RED + "This command is blocked while in combat.");
            }
        }
    }
}
