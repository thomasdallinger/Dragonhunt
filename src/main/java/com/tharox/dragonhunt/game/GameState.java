package com.tharox.dragonhunt.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class GameState {
    // location of the first player entering the NETHER (null as long as no player enters the NETHER)
    public static Location firstNetherEntry = null;

    // location of the END spawn platform (always has the same coordinates)
    public static Location endPlatform = new Location(Bukkit.getWorld("world_the_end"), 100, 50, 0);

    // all items a player keeps on death stored in a HashMap
    public static HashMap<Player, ItemStack[]> itemsToKeepOnDeath = new HashMap<>();

    // HashMaps which contain which food / resource a player is supposed to get from killing entities / mining blocks
    public static HashMap<Player, ItemStack> food = new HashMap<>();
    public static HashMap<Player, List<ItemStack>> resources = new HashMap<>();

    /**
     * Used in LevelUp.
     *
     * @param player player who is supposed to get the item
     * @param item   item the player should get
     */
    public static void putItemInInventory(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(item);

        if(!didNotFit.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), didNotFit.get(0));
            // index 0 = max. only 1 item in the HashMap
        }
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
