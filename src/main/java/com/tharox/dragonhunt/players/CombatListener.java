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
import org.bukkit.entity.Player;
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
    public static HashMap<UUID, BossBar> bossBars = new HashMap<>();

    public static BossBar bossBar = Bukkit.createBossBar(HexColor.RED + "You are currently in Combat!",
            BarColor.RED, BarStyle.SEGMENTED_10);

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
        AbstractArrow arrow;
        ProjectileSource shooter;
        Player damager;
        Player target;

        if (event.getEntity() instanceof Player)
            target = (Player) event.getEntity();
        else {
            return;
        }

        if (event.getDamager() instanceof AbstractArrow) {
            arrow = (AbstractArrow) event.getDamager();
            shooter = arrow.getShooter();

            if (shooter instanceof Player) {
                damager = (Player) shooter;
            } else return;

        } else if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else return;

        // Once the grace period has ended, the in combat feature comes into effect.
        if (Main.getInstance().getTimer().getTime() < GameState.gracePeriodTime) return;

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO InCombat (GameID, Target, Damager, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + target.getName() + "', '"
                    + damager.getName() + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        combatList.put(target.getUniqueId(), 30);
        combatList.put(damager.getUniqueId(), 30);

        bossBars.put(target.getUniqueId(), bossBar);
        bossBars.get(target.getUniqueId()).addPlayer(target);
        bossBars.put(damager.getUniqueId(), bossBar);
        bossBars.get(damager.getUniqueId()).addPlayer(damager);

    }

    public void onInterval() {
        HashMap<UUID, Integer> temp = new HashMap<>();

        try {
            for (UUID id : combatList.keySet()) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) continue;

                if (player.isDead()) {
                    temp.remove(id);
                    bossBars.get(player.getUniqueId()).removePlayer(player);
                    continue;
                }

                int timer = combatList.get(id) - 1;

                if (timer > 0) {
                    temp.put(id, timer);
                    bossBars.get(id).setProgress(timer / 31D);
                } else {
                    temp.remove(id);
                    bossBars.get(player.getUniqueId()).removePlayer(player);

                    player.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + "You are no longer in combat.");
                }

                bossBars.get(player.getUniqueId()).setVisible(true);
            }
        } catch (ConcurrentModificationException exception) {
            // do nothing
        }

        combatList = temp;
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {
        if (combatList.containsKey(event.getPlayer().getUniqueId())) {
            List<String> commands = Arrays.asList("/pos", "/date", "/top", "/hub", "/l", "/lobby");

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
