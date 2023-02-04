package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Statement;
import java.util.*;

public class CombatListener implements Listener {
    // contains the UUID of players who are in combat
    public static HashMap<UUID, Integer> combatList = new HashMap<>();

    static BossBar bossBar = Bukkit.createBossBar(HexColor.RED + "You are currently in Combat!",
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

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player target = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

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

            bossBar.addPlayer(target);
            bossBar.addPlayer(damager);
        }
    }

    public void onInterval() {
        HashMap<UUID, Integer> temp = new HashMap<>();

        try {
            for (UUID id : combatList.keySet()) {
                Player player = Bukkit.getPlayer(id);
                if (player == null) return;
                int timer = combatList.get(id) - 1;

                if (timer > 0) {
                    temp.put(id, timer);
                    bossBar.setProgress(timer / 31D);
                } else {
                    temp.remove(id);
                    bossBar.removePlayer(player);

                    player.sendMessage(ChatColor.YELLOW + "☻" + ChatColor.DARK_GRAY + " | "
                            + HexColor.GREEN + "You are no longer in combat.");
                }

                bossBar.setVisible(true);
            }
        } catch (ConcurrentModificationException exception) {
            // do nothing
        }

        combatList = temp;
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {
        if (combatList.containsKey(event.getPlayer().getUniqueId())) {
            List<String> commands = Arrays.asList("/pos", "/date", "/spec", "/respawn", "/top");

            String[] parts = event.getMessage().split(" ");
            String cmd = parts[0].toLowerCase();

            if (commands.contains(cmd)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Prefixes.getErrorPrefix()
                        + ChatColor.GRAY + "This command is blocked while in combat.");
            }
        }
    }
}
