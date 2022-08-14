package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.entities.PermanentWitherSkeletons;
import com.tharox.dragonhunt.grappler.Grappler;
import com.tharox.dragonhunt.modifiers.classes.Diamonds;
import com.tharox.dragonhunt.modifiers.classes.EasyDifficulty;
import com.tharox.dragonhunt.modifiers.classes.EightHearts;
import com.tharox.dragonhunt.modifiers.classes.HardDifficulty;
import com.tharox.dragonhunt.players.PermanentEffect;
import com.tharox.dragonhunt.spectate.Spectate;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Objects;

public class GameConfiguration {
    /**
     * Sets different parameters for the OVERWORLD such as difficulty, daytime and the world border.
     */
    public void configureOverworld() {
        World overworld = Bukkit.getServer().getWorld("world");

        if (overworld == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix() + HexColor.RED + "World name is incorrect.");
            return;
        }

        if (EasyDifficulty.active)
            overworld.setDifficulty(Difficulty.EASY);
        else if (HardDifficulty.active)
            overworld.setDifficulty(Difficulty.HARD);
        else
            overworld.setDifficulty(Difficulty.NORMAL);

        WorldBorder worldBorder = overworld.getWorldBorder();
        worldBorder.setCenter(new Location(overworld, 0, 0, 0));
        worldBorder.setSize(6000);
        worldBorder.setDamageAmount(0);
    }

    /**
     * Sets the world border and difficulty for the NETHER
     */
    public void configureNether() {
        World nether = Bukkit.getServer().getWorld("world_nether");

        if (nether == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix()
                    + HexColor.RED + "World Nether name is incorrect.");
            return;
        }

        if (EasyDifficulty.active)
            nether.setDifficulty(Difficulty.EASY);
        else if (HardDifficulty.active)
            nether.setDifficulty(Difficulty.HARD);
        else
            nether.setDifficulty(Difficulty.NORMAL);

        WorldBorder worldBorder = nether.getWorldBorder();
        worldBorder.setCenter(new Location(nether, 0, 0, 0));
        worldBorder.setSize(4000);
        worldBorder.setDamageAmount(0);
    }

    /**
     * Sets the difficulty for the END
     */
    public void configureEnd() {
        World end = Bukkit.getServer().getWorld("world_the_end");

        if (end == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix()
                    + HexColor.RED + "World End name is incorrect.");
            return;
        }

        if (EasyDifficulty.active)
            end.setDifficulty(Difficulty.EASY);
        else if (HardDifficulty.active)
            end.setDifficulty(Difficulty.HARD);
        else
            end.setDifficulty(Difficulty.NORMAL);
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
            player.setSaturation(4); // players shouldn't loose hunger points instantly
            player.setExp(0);     // resets Bar
            player.setLevel(0);   // resets Number

            // set the player's health to eight hearts if the modifier is active
            if (EightHearts.active) {
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(16);
                player.setHealth(16);
            }

            // clears the player's inventory
            player.getInventory().clear();

            // adds the TRACKER, ELYTRA, START-EQUIPMENT (Shield) and GRAPPLER to the player's inventory
            player.getInventory().setItem(7, Main.getInstance().getTracker().givePlayerTracker());
            player.getInventory().setChestplate(addItem(new ItemStack(Material.ELYTRA)));
            player.getInventory().setItemInOffHand(addItem(new ItemStack(Material.SHIELD)));
            new Grappler(player);

            // HashMap which prevents players from executing the command while the SPECTATE is still active
            Spectate.commandUsable.put(player, true);

            // every player starts with the base items for food and resources
            // the item and amount can be upgraded using FOOD or RESOURCE UPGRADES respectively
            GameState.food.put(player, new ItemStack(Material.COOKED_RABBIT, 2));

            // diamonds drop from IRON ORES, GOLD ORES and COPPER ORES too if the modifier is active
            if (Diamonds.active) {
                GameState.resources.put(player, List.of(
                        new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.DIAMOND),
                        new ItemStack(Material.DIAMOND)));
            } else {
                GameState.resources.put(player, List.of(
                        new ItemStack(Material.IRON_INGOT),
                        new ItemStack(Material.GOLD_INGOT),
                        new ItemStack(Material.DIAMOND)));
            }

            // set default TRACKER values, nobody is tracked when the player receives the TRACKER
            Main.getInstance().getTracker().playerTracksName.put(player, null);
            Main.getInstance().getTracker().playerTracksIndex.put(player, 0);
        }

        // active if any of the specified modifiers are active
        new PermanentEffect();
        new PermanentWitherSkeletons();
    }

    /**
     * @param item ItemStack to be modified and returned
     * @return ItemStack to be added to the players inventory
     */
    private ItemStack addItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        // making the item unbreakable while looking enchanted
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (item.getType() == Material.SHIELD) {
            meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Shield");
        } else if (item.getType() == Material.ELYTRA) {
            meta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Elytra");
        }

        item.setItemMeta(meta);
        return item;
    }
}
