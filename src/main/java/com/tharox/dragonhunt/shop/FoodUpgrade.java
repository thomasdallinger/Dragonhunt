package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class FoodUpgrade {
    public static HashMap<Player, Integer> FoodSource = new HashMap<>();
    public static HashMap<Player, Integer> FoodAmount = new HashMap<>();
    public Player player;
    public int level;

    public FoodUpgrade(Player player, String upgrade, int level) {
        this.player = player;
        this.level = level;

        //if food source: 4 -> 4 means access to purchase level 3
        if (upgrade.contains("Food Source")) {
            ItemStack base = new ItemStack(Material.RABBIT);
            ItemStack level1 = new ItemStack(Material.COOKED_RABBIT);
            ItemStack level2 = new ItemStack(Material.COOKED_MUTTON);
            ItemStack level3 = new ItemStack(Material.COOKED_BEEF);

            switch (level) {
                case 1:
                    GameState.food.put(player, level1);
                    replaceFood(base.getType(), level1.getType());
                    break;
                case 2:
                    GameState.food.put(player, level2);
                    replaceFood(level1.getType(), level2.getType());
                    break;
                case 3:
                    GameState.food.put(player, level3);
                    replaceFood(level2.getType(), level3.getType());
                    break;
            }

            if (FoodSource.get(player) <= 3) {
                FoodSource.put(player, FoodSource.get(player) + 1);
            }
        } else if (upgrade.contains("Food Amount")) {
            //level 1 = double food, level 2 = triple food
            GameState.food.put(player, new ItemStack(GameState.food.get(player).getType(), level + 1));

            if (FoodAmount.get(player) <= 2) {
                FoodAmount.put(player, FoodAmount.get(player) + 1);
            }
        }
    }

    private void replaceFood(Material oldFood, Material newFood) {
        int amount;
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (item.getType().equals(oldFood)) {
                    amount = item.getAmount();
                    item.setType(newFood); //resets item data -> amount needs to set again
                    item.setAmount(amount);
                }
            }
        }
    }
}
