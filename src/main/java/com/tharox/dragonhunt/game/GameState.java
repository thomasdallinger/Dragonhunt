package com.tharox.dragonhunt.game;

import com.tharox.dragonhunt.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GameState {
    public static HashMap<Player, Kit> kit = new HashMap<>();

    public static World nether = Bukkit.getServer().getWorlds().get(1);
    public static List<Location> dungeonLocations = Arrays.asList(
            new Location(nether, -61.5, 87, -38.5, (float) -156.6, (float) 5.0),
            new Location(nether, -61.5, 87, -38.5, (float) -156.6, (float) 5.0)
    );
    public static Location dungeonLocation;
    //Location of the nether portal in the overworld
    //saved before going into the nether
    public static HashMap<Player, Location> overworldLocation = new HashMap<>();

    public static HashMap<Player, ItemStack[]> itemsToKeepOnDeath = new HashMap<>();

    public static boolean minOnePlayerInEnd = false;

    /**
     * Used in EntityDeathListener, ShopListener.
     * @param player player who is supposed to get the item
     * @param item item the player should get
     */
    public static boolean sameItemAlreadyInInventory(Player player, ItemStack item) {
        for (ItemStack invetoryItem : player.getInventory()) {
            if(invetoryItem != null && invetoryItem.getItemMeta() != null) {
                if ((invetoryItem.getType() == item.getType()) && !invetoryItem.getItemMeta().getDisplayName().contains("Kit Ability")) {
                    if (invetoryItem.getMaxStackSize() >= (invetoryItem.getAmount() + item.getAmount())) {
                        player.getInventory().addItem(item);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
