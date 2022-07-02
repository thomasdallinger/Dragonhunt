package com.tharox.dragonhunt.upgrades;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class ArrowUpgrade {
    public static HashMap<Player, Integer> MoreArrows = new HashMap<>();
    public static HashMap<Player, Integer> EffectArrows = new HashMap<>();
    public Player player;

    public ArrowUpgrade(Player player, String upgrade) {
        this.player = player;

        if (upgrade.contains("More Arrows")) {

            if (MoreArrows.get(player) <= 3) {
                MoreArrows.put(player, MoreArrows.get(player) + 1);
            }
        } else if (upgrade.contains("Effect Arrows")) {

            if (EffectArrows.get(player) <= 3) {
                EffectArrows.put(player, EffectArrows.get(player) + 1);
            }
        }
    }
}
