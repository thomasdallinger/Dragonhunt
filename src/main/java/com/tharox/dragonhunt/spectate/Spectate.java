package com.tharox.dragonhunt.spectate;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Spectate {
    public static HashMap<Player, Location> spectateLocation = new HashMap<>();
    public static HashMap<Player, Boolean> commandUsable = new HashMap<>();

    public Spectate(Player player) {
        activateSpectate(player);
    }

    private void activateSpectate(Player player) {
        final int[] countdown = {4};
        List<PotionEffect> effects = new ArrayList();
        effects.add(new PotionEffect(PotionEffectType.SLOW, 40, 9));
        effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 40, 9));
        effects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 9));
        effects.add(new PotionEffect(PotionEffectType.JUMP, 40, -9));

        commandUsable.put(player, false);
        spectateLocation.put(player, player.getLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1, 1);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Prefixes.getAllPrefix() + ChatColor.YELLOW + player.getName()
                    + ChatColor.GRAY + " uses a Spectate.");
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (countdown[0] <= 0) {
                    player.setGameMode(GameMode.SPECTATOR);
                    spectateDurationLeft(player);
                    cancel();
                    return;
                }

                player.spawnParticle(Particle.PORTAL, player.getLocation(), 50);
                player.sendMessage(Prefixes.getCoinPrefix() + ChatColor.GRAY + "Spectate starts in "
                        + ChatColor.YELLOW + countdown[0] + ChatColor.GRAY + " seconds.");
                player.addPotionEffects(effects);
                countdown[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    private void spectateDurationLeft(Player player) {
        final int[] duration = {12};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (duration[0] <= 0) {
                    player.performCommand("pos Spectate" + getSpectateAmount(player));
                    player.teleport(Spectate.spectateLocation.get(player));
                    player.setGameMode(GameMode.SURVIVAL);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    commandUsable.put(player, true);
                    cancel();
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString()
                        + ChatColor.BOLD + duration[0] + ChatColor.YELLOW + ChatColor.BOLD + " SECONDS LEFT"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                duration[0]--;
            }

        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    /**
     * @return the amount of spectates purchased by a player in this game
     */
    public static int getSpectateAmount(Player player) {
        final int[] amount = {0};

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "SELECT COUNT(*) FROM Spectate WHERE GameID = '" + DbValues.getGameID() + "' " +
                    "AND Player = '" + player.getName() + "'";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    amount[0] = rs.getInt(i);
                }
            }
            rs.close();
            stmt.close();
        });

        return amount[0];
    }
}
