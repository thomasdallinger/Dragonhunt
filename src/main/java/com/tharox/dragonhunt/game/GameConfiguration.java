package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.modifiers.classes.EndlessNight;
import com.tharox.dragonhunt.modifiers.classes.Heartbroken;
import com.tharox.dragonhunt.players.PointsListener;
import com.tharox.dragonhunt.respawn.RespawnAnchor;
import com.tharox.dragonhunt.scout.Scout;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;

public class GameConfiguration {
    /**
     * Sets different parameters for the OVERWORLD such as the daytime and the world border.
     */
    public void configureOverworld(double x, double z) {
        World overworld = Bukkit.getServer().getWorld("world");

        if (overworld == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix() + "World name is incorrect.");
            return;
        }

        overworld.setDifficulty(Difficulty.NORMAL);
        overworld.setGameRule(GameRule.KEEP_INVENTORY, true);
        overworld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        if (!EndlessNight.active)
            overworld.setTime(0);
        else
            new PermanentNight();

        WorldBorder worldBorder = overworld.getWorldBorder();
        worldBorder.setCenter(new Location(overworld, x, 0, z));
        worldBorder.setSize(2000);
        worldBorder.setDamageAmount(0);
    }

    /**
     * Sets the world border and game rules for the NETHER.
     */
    public void configureNether() {
        World nether = Bukkit.getServer().getWorld("world_nether");

        if (nether == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix()
                    + HexColor.RED + "World Nether name is incorrect.");
            return;
        }

        nether.setDifficulty(Difficulty.NORMAL);
        nether.setGameRule(GameRule.KEEP_INVENTORY, true);
        nether.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        WorldBorder worldBorder = nether.getWorldBorder();
        worldBorder.setCenter(new Location(nether, 0, 0, 0));
        worldBorder.setSize(4000);
        worldBorder.setDamageAmount(0);
    }

    /**
     * Sets game rules for the END.
     */
    public void configureEnd() {
        World end = Bukkit.getServer().getWorld("world_the_end");

        if (end == null) {
            Bukkit.getServer().broadcastMessage(Prefixes.getErrorPrefix()
                    + HexColor.RED + "World End name is incorrect.");
            return;
        }

        end.setDifficulty(Difficulty.NORMAL);
        end.setGameRule(GameRule.KEEP_INVENTORY, true);
        end.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    /**
     * Sets different parameters for each PLAYER.
     */
    public void configurePlayer(Location startLocation) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // teleport the player
            player.teleport(startLocation);

            // non of the players has selected a kit yet
            GameState.kitInventoryClose.put(player, false);

            // give the player a score of 0
            PointsListener.scorePerPlayer.put(player.getUniqueId(), 0);

            // when the game starts, players won't have the ability to scout
            Scout.scoutAvailable.put(player, false);

            // removes all active potion effects players may have
            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());

            // resets food values and XP levels
            player.setFoodLevel(20);
            player.setSaturation(4); // players shouldn't loose hunger points instantly
            player.setExp(0);     // resets Bar
            player.setLevel(0);   // resets Number

            // set the player's health to -2 hearts if the modifier is active
            if (Heartbroken.active) {
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(
                        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() - 4.0);
                player.setHealth(player.getHealth() - 4.0);
            }

            // clears the player's inventory
            player.getInventory().clear();

            // adds general items to the player's inventory
            player.getInventory().setItem(6, RespawnAnchor.createItem(player));
            player.getInventory().setItem(7, Main.getInstance().getTracker().createTracker());
            player.getInventory().setItemInOffHand(GameState.addItem(new ItemStack(Material.SHIELD)));

            // set default TRACKER values, nobody is tracked when the player receives the TRACKER
            Main.getInstance().getTracker().playerTracksName.put(player, null);
            Main.getInstance().getTracker().playerTracksIndex.put(player, 0);
        }
    }
}
