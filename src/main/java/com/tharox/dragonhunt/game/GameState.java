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
    public static int gracePeriodTime = 60;

    // modifier inventory can be closed after the first 15 seconds before the game starts
    public static boolean modifierInventoryClose = false;

    // kit inventory can be closed after a kit has been selected
    public static HashMap<Player, Boolean> kitInventoryClose = new HashMap<>();

    // which player has selected which kit
    public static HashMap<Player, String> selectedKit = new HashMap<>();

    // which player has an active kit ability
    public static HashMap<Player, Boolean> actionBarNeededForKitAbility = new HashMap<>();

    // location of the first player entering the NETHER (null as long as no player enters the NETHER)
    public static Location firstNetherEntry = null;

    // whether the players have reached the END dimension or not (set to true once the first player is in the END)
    public static boolean playersReachedEnd = false;

    // location of the END spawn platform (always has the same coordinates)
    public static Location endPlatform = new Location(Bukkit.getWorld("world_the_end"), 100, 50, 0);

    /**
     * @return a list that contains chest and minecart loot
     */
    public static ArrayList<ItemStack> getChestLoot() {
        ArrayList<ItemStack> chestLoot = new ArrayList<>();
        chestLoot.add(new ItemStack(Material.OBSIDIAN, 2));
        chestLoot.add(new ItemStack(Material.DIAMOND, 2));
        chestLoot.add(new ItemStack(Material.GOLD_INGOT, 6));
        chestLoot.add(new ItemStack(Material.IRON_INGOT, 6));

        return chestLoot;
    }

    /**
     * Sets messages displaying the loot a player got from clicking a chest / minecart.
     */
    public static void setChestLootMessages(Player player) {
        player.sendMessage(ChatColor.GRAY + "2x " + ChatColor.DARK_PURPLE + "Obsidian");
        player.sendMessage(ChatColor.GRAY + "2x " + ChatColor.AQUA + "Diamond");
        player.sendMessage(ChatColor.GRAY + "6x " + ChatColor.GOLD + "Gold Ingot");
        player.sendMessage(ChatColor.GRAY + "6x " + ChatColor.WHITE + "Iron Ingot");
    }

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
                    + ChatColor.RED + "Item dropped on the ground.");
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
