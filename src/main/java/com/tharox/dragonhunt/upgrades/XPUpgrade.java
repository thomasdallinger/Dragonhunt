package com.tharox.dragonhunt.upgrades;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class XPUpgrade {
    public static HashMap<Player, Integer> MoreXP = new HashMap<>();
    Player player;

    public XPUpgrade(Player player) {
        this.player = player;

        if (MoreXP.get(player) <= 3)
            MoreXP.put(player, MoreXP.get(player) + 1);
    }
}
