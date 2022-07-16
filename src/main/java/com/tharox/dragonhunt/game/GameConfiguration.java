package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.grappler.Grappler;
import com.tharox.dragonhunt.spectate.Spectate;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class GameConfiguration {
    /**
     * Sets different parameters for the OVERWORLD such as difficulty, daytime and the world border.
     */
    public void configureOverworld() {
        World overworld = Bukkit.getServer().getWorld("world");

        if (overworld == null) {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "ERROR: World name is incorrect.");
            return;
        }

        overworld.setDifficulty(Difficulty.NORMAL);

        WorldBorder worldBorder = overworld.getWorldBorder();
        worldBorder.setCenter(new Location(overworld, 0, 0, 0));
        worldBorder.setSize(6000);
        worldBorder.setDamageAmount(0);
    }

    /**
     * Sets the world border for the NETHER
     */
    public void configureNether() {
        World nether = Bukkit.getServer().getWorld("world_nether");

        if (nether == null) {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "ERROR: World Nether name is incorrect.");
            return;
        }

        WorldBorder worldBorder = nether.getWorldBorder();
        worldBorder.setCenter(new Location(nether, 0, 0, 0));
        worldBorder.setSize(4000);
        worldBorder.setDamageAmount(0);
    }

    /**
     * Sets different parameters for each PLAYER.
     */
    public void configurePlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // removes all active potion effects players may have
            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());

            // resets food values and XP levels
            player.setFoodLevel(20);
            player.setSaturation(4); //players shouldn't loose hunger points instantly
            player.setExp(0);     //resets Bar
            player.setLevel(0);   //resets Number

            // clears the player's inventory
            player.getInventory().clear();

            // adds the TRACKER, ELYTRA, START-EQUIPMENT (Shield) and GRAPPLER to the player's inventory
            player.getInventory().setItem(7, Main.getInstance().getTracker().givePlayerTracker());
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
            player.getInventory().setItemInOffHand(addItem(new ItemStack(Material.SHIELD)));
            new Grappler(player);

            // give the player 3 SPECTATE TOKENS which can be used throughout the game
            Spectate.spectatesLeft.put(player, 3);
            // HashMap which prevents players from executing the command multiple times without a delay
            Spectate.commandUsable.put(player, true);

            // every player starts with the base items for food and resources
            // these can be upgraded using FOOD or RESOURCE UPGRADES respectively
            GameState.food.put(player, new ItemStack(Material.COOKED_RABBIT));
            GameState.resources.put(player, List.of(
                    new ItemStack(Material.IRON_INGOT),
                    new ItemStack(Material.GOLD_INGOT),
                    new ItemStack(Material.DIAMOND)));

            // set default TRACKER values, nobody is tracked when the player receives the TRACKER
            Main.getInstance().getTracker().playerTracksStructure.put(player, null);
            Main.getInstance().getTracker().playerTracksIndex.put(player, 0);
        }
    }

    /**
     * @param item ItemStack to be modified and returned
     * @return ItemStack to be added to the players inventory
     */
    private ItemStack addItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        if (item.getType() == Material.SHIELD) { // making the shield unbreakable while looking enchanted
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.setUnbreakable(true);
            meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Shield");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }
}
