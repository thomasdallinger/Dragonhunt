package com.tharox.dragonhunt.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class GameState {
    // modifier inventory can be closed after the first 15 seconds before the game starts
    public static boolean modifierInventoryClose = false;

    // kit inventory can be closed after a kit has been selected
    public static HashMap<Player, Boolean> kitInventoryClose = new HashMap<>();

    // which player has selected which kit
    public static HashMap<Player, String> selectedKit = new HashMap<>();

    // location of the first player entering the NETHER (null as long as no player enters the NETHER)
    public static Location firstNetherEntry = null;

    // whether the players have reached the END dimension or not (set to true once the first player is in the END)
    public static boolean playersReachedEnd = false;

    // location of the END spawn platform (always has the same coordinates)
    public static Location endPlatform = new Location(Bukkit.getWorld("world_the_end"), 100, 50, 0);

    // distance of the NETHER entry portal in the NETHER to the nearest (looted or unlooted) Fortress and Bastion
    public static int distanceScore = -1; // stays at -1 if none of the players reach the NETHER

    // counts the trees destroyed by each player (used to drop apples not randomly)
    public static HashMap<Player, Integer> treesDestroyed = new HashMap<>();

    /**
     * Used in LevelUp.
     *
     * @param player player who is supposed to get the item
     * @param item   item the player should get
     */
    public static void putItemInInventory(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(item);

        if (!didNotFit.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), didNotFit.get(0));
            // index 0 = max. only 1 item in the HashMap
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + HexColor.RED + "Inventory full! "
                    + ChatColor.GRAY + "Item dropped on the ground.");
        }
    }

    /**
     * @param item ItemStack to be modified and returned
     * @return ItemStack to be added to the players inventory
     */
    public static ItemStack addItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        // enchanted look
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (item.getType() == Material.SHIELD) {
            meta.setDisplayName(ChatColor.AQUA + "Shield");
        } else if (item.getType() == Material.ELYTRA) {
            meta.setDisplayName(ChatColor.AQUA + "Elytra");
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Moves all players on this server to the lobby server.
     * Updates the server status in the database (no longer running).
     * Shuts down the server.
     */
    public static void movePlayersToLobby() {
        new BukkitRunnable() {
            int duration = 15; // move all players, who where on the server, within 15 seconds

            @Override
            public void run() {
                ArrayList<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());

                // no more players to move or time is up
                if (playerList.size() == 0 || duration <= 0) {
                    DbValues.updateServerStatus();
                    Bukkit.getServer().shutdown();
                    this.cancel();
                }

                // move one player at every odd second
                // max. amount of players: 8
                if (duration % 2 == 1) {
                    playerList.get(0).playSound(playerList.get(0).getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF("lobby");
                    playerList.get(0).sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
                }

                duration -= 1;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
