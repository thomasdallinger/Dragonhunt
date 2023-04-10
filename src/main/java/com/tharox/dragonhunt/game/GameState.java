package com.tharox.dragonhunt.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.modifiers.classes.DoubleChestLoot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameState {
    public static int gracePeriodTime = 30;

    // modifier inventory can be closed after the first 15 seconds before the game starts
    public static boolean modifierInventoryClose = false;

    // kit inventory can be closed after a kit has been selected
    public static HashMap<Player, Boolean> kitInventoryClose = new HashMap<>();

    // which player has selected which kit
    public static HashMap<Player, String> selectedKit = new HashMap<>();

    // which player has an active kit ability (value: duration in seconds)
    public static HashMap<Player, Integer> kitAbilityActive = new HashMap<>();

    // stores the cooldown reduction in seconds acquired by each player
    public static HashMap<Player, Integer> kitCooldownReduction = new HashMap<>();

    // location of the first player entering the NETHER (null as long as no player enters the NETHER)
    public static Location firstNetherEntry = null;

    // whether the players have reached the END dimension or not (set to true once the first player is in the END)
    public static boolean playersReachedEnd = false;

    // location of the END spawn platform (always has the same coordinates)
    public static Location endPlatform = new Location(Bukkit.getWorld("world_the_end"), 100, 50, 0);

    /**
     * @return a list of UUIDs ordered by the points each player has descending
     */
    public static List<UUID> sortPlayersByPoints(HashMap<UUID, Integer> map) {
        // Create a list of Map.Entry objects from the HashMap
        List<Map.Entry<UUID, Integer>> list = new ArrayList<>(map.entrySet());

        // Sort the list using a custom Comparator
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // Create a new ArrayList to store the sorted keys
        List<UUID> sortedKeys = new ArrayList<>();

        // Add the sorted keys to the ArrayList
        for (Map.Entry<UUID, Integer> entry : list) {
            sortedKeys.add(entry.getKey());
        }

        // Return the sorted keys
        return sortedKeys;
    }

    /**
     * @return a list that contains chest and minecart loot
     */
    public static ArrayList<ItemStack> getChestLoot(Player player) {
        Coins.addXP(player, Coins.chestXP);

        int multiplier = 1;
        if (DoubleChestLoot.active) multiplier = 2;

        ArrayList<ItemStack> chestLoot = new ArrayList<>();
        chestLoot.add(new ItemStack(Material.DIAMOND, 2 * multiplier));
        chestLoot.add(new ItemStack(Material.OBSIDIAN, 2 * multiplier));
        chestLoot.add(new ItemStack(Material.IRON_INGOT, 4 * multiplier));

        for (ItemStack item : chestLoot) {
            printItemReceivedMessage(player, item);
        }

        return chestLoot;
    }

    /**
     * addItem(item) returns a HashMap<Integer, ItemStack> containing items
     * that did not fit in the player's inventory.
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
            return;
        }

        // Print every item except wood from trees
        if (item.getType() != Material.OAK_LOG) {
            printItemReceivedMessage(player, item);
        }
    }

    /**
     * Notify the player that he/she received an item.
     */
    public static void printItemReceivedMessage(Player player, ItemStack item) {

        String lowercaseString = item.getType().toString().replace("_", " ").toLowerCase();
        String capitalizedString = "";

        // Split the string into an array of words, using any whitespace characters as delimiters.
        String[] words = lowercaseString.split("\\s+");

        for (String word : words) {
            String firstLetter = word.substring(0, 1);
            String restOfWord = word.substring(1);
            capitalizedString += firstLetter.toUpperCase() + restOfWord + " ";
        }

        player.sendMessage(HexColor.GREEN + "+" + item.getAmount() + " "
                + ChatColor.WHITE + capitalizedString.trim());
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

    public static ItemStack createRockets(int amount) {
        ItemStack rockets = new ItemStack(Material.FIREWORK_ROCKET, amount);

        FireworkMeta meta = (FireworkMeta) rockets.getItemMeta();
        if (meta == null) return null;

        for (int i = 0; i < 7; i++) {
            meta.addEffect(FireworkEffect.builder().withColor(Color.RED).withColor(Color.ORANGE).
                    with(FireworkEffect.Type.BALL).build());
        }

        meta.setPower(1);
        rockets.setItemMeta(meta);
        return rockets;
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
