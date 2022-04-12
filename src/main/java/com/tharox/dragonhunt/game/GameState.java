package com.tharox.dragonhunt.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GameState {
    // a list of all available NETHER DUNGEONS
    // one of which is chosen for a particular game
    public static World nether = Bukkit.getServer().getWorlds().get(1);
    public static List<Location> dungeonLocations = Arrays.asList(
            new Location(nether, -61.5, 87, -38.5, (float) -156.6, (float) 5.0),
            new Location(nether, -61.5, 87, -38.5, (float) -156.6, (float) 5.0)
    );

    // location of the chosen NETHER DUNGEON
    public static Location dungeonLocation;

    // location of the nether portal in the OVERWORLD, saved before a player heads into the NETHER
    public static HashMap<Player, Location> overworldLocation = new HashMap<>();

    // all items a player keeps on death stored in a HashMap
    public static HashMap<Player, ItemStack[]> itemsToKeepOnDeath = new HashMap<>();

    // a flag which is true if at least one player has reached the END dimension
    public static boolean minOnePlayerInEnd = false;

    // HashMaps which contain which food / resource a player is supposed to get from killing entities / mining blocks
    public static HashMap<Player, ItemStack> food = new HashMap<>();
    public static HashMap<Player, List<ItemStack>> resources = new HashMap<>();

    /**
     * Used in EntityDeathListener.
     * @param player player who is supposed to get the item
     * @param item item the player should get
     */
    public static boolean sameItemAlreadyInInventory(Player player, ItemStack item) {
        for (ItemStack inventoryItem : player.getInventory()) {
            if(inventoryItem != null && inventoryItem.getItemMeta() != null) {
                if ((inventoryItem.getType() == item.getType())) {
                    if (inventoryItem.getMaxStackSize() >= (inventoryItem.getAmount() + item.getAmount())) {
                        player.getInventory().addItem(item);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Moves all players on this server to the lobby server.
     * Updates the server status in the database (no longer running).
     * Shuts down the server.
     */
    public static void movePlayersToLobby() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("lobby");
            player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
        }

        DbValues.updateServerStatus();
        Bukkit.getServer().shutdown();
    }
}
