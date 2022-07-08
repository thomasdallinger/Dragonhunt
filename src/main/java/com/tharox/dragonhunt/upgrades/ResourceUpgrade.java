package com.tharox.dragonhunt.upgrades;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class ResourceUpgrade extends ItemUpgrade {
    public static HashMap<Player, Integer> ResourceAmount = new HashMap<>();

    public ResourceUpgrade(Player player, String upgrade, int level) {
        this.player = player;
        this.level = level;

        if (upgrade.contains("Resource Amount")) {
            // level 1 = double resources, level 2 = triple resources, level 3 = quadruple resources
            GameState.resources.put(player, List.of(
                    new ItemStack(GameState.resources.get(player).get(0).getType(), level + 1),
                    new ItemStack(GameState.resources.get(player).get(1).getType(), level + 1),
                    new ItemStack(GameState.resources.get(player).get(2).getType(), level + 1)));

            if (ResourceAmount.get(player) <= 3)
                ResourceAmount.put(player, ResourceAmount.get(player) + 1);
        }
    }
}