package com.tharox.dragonhunt.players;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BossBarManager {
    public static HashMap<UUID, BossBar> playerBossBars = new HashMap<>();

    public static void addBossBar(Player player, BossBar bossBar) {
        bossBar.addPlayer(player);
        playerBossBars.put(player.getUniqueId(), bossBar);
    }

    public static boolean hasBossBar(Player player) {
        return playerBossBars.containsKey(player.getUniqueId());
    }

    public static BossBar getBossBar(Player player) {
        if (hasBossBar(player)) {
            return playerBossBars.get(player.getUniqueId());
        }

        return null;
    }

    public static void removeBossBar(Player player) {
        BossBar bossBar = playerBossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }
}
