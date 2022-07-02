package com.tharox.dragonhunt.upgrades;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class ResourceUpgrade extends ItemUpgrade {
    public static HashMap<Player, Integer> SmeltedResource = new HashMap<>();
    public static HashMap<Player, Integer> ResourceAmount = new HashMap<>();

    public ResourceUpgrade(Player player, String upgrade, int level) {
        this.player = player;
        this.level = level;

        if (upgrade.contains("Smelted Resource")) {
            switch (level) {
                case 1:
                    GameState.resources.put(player, List.of(
                            new ItemStack(Material.IRON_INGOT, GameState.resources.get(player).get(0).getAmount()),
                            new ItemStack(Material.RAW_GOLD, GameState.resources.get(player).get(1).getAmount())));
                    replaceItem(Material.RAW_IRON, Material.IRON_INGOT);
                    break;
                case 2:
                    GameState.resources.put(player, List.of(
                            new ItemStack(Material.IRON_INGOT, GameState.resources.get(player).get(0).getAmount()),
                            new ItemStack(Material.GOLD_INGOT, GameState.resources.get(player).get(1).getAmount())));
                    replaceItem(Material.RAW_GOLD, Material.GOLD_INGOT);
                    break;
            }

            if (SmeltedResource.get(player) <= 2)
                SmeltedResource.put(player, SmeltedResource.get(player) + 1);
        } else if (upgrade.contains("Resource Amount")) {
            //level 1 = double resources, level 2 = triple resources
            GameState.resources.put(player, List.of(
                    new ItemStack(GameState.resources.get(player).get(0).getType(), level + 1),
                    new ItemStack(GameState.resources.get(player).get(1).getType(), level + 1)));

            if (ResourceAmount.get(player) <= 2)
                ResourceAmount.put(player, ResourceAmount.get(player) + 1);
        }
    }
}