package com.tharox.dragonhunt.tracker;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.PlayerDeathListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StructureSearchResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tracker functionality
 * Tracker item
 * new Tracker in main - getTracker(), 1 instance for all players
 */
public class Tracker implements Listener {
    public Map<Player, String> playerTracksName = new HashMap<>();
    public Map<Player, Location> playerTracksLocation = new HashMap<>();
    public Map<Player, Integer> playerTracksIndex = new HashMap<>();

    public Tracker() {
        run();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        World world = player.getWorld();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        int index = playerTracksIndex.getOrDefault(player, 0);

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        // Left-Click: Track Player
        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Tracker")) {
                LinkedList<Player> playerList = new LinkedList<>(Bukkit.getServer().getOnlinePlayers());
                List<Player> allOtherPlayers = playerList.stream().filter(o -> !(o.getName().equals(player.getName())))
                        .collect(Collectors.toList());

                if (allOtherPlayers.size() == 0) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "No other players found.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                index++;    // switch the located player

                if (index > allOtherPlayers.size() - 1)
                    index = 0;

                playerTracksName.put(player, "[P] " + allOtherPlayers.get(index).getName());
                playerTracksLocation.put(player, allOtherPlayers.get(index).getLocation());
                player.sendMessage(Prefixes.getTrackingPrefix() + ChatColor.GRAY + "Tracking "
                        + ChatColor.WHITE + playerTracksName.get(player).substring(4) + ChatColor.GRAY + ".");

                playerTracksIndex.replace(player, index);
                return;
            }
        }

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Tracker")) {

                if (player.getWorld().getEnvironment() == World.Environment.THE_END) return;

                // a list of all structures (and locations) trackable in the world the player is in
                LinkedList<String> structureList = new LinkedList<>();
                LinkedList<Location> locationList = new LinkedList<>();

                if (world.getName().equals("world")) {
                    addStructureToLists(player, Structure.MINESHAFT, "Mineshaft", structureList,
                            locationList);
                    addStructureToLists(player, Structure.VILLAGE_PLAINS, "Plains Village", structureList,
                            locationList);
                    addStructureToLists(player, Structure.ANCIENT_CITY, "Ancient City", structureList,
                            locationList);

                } else if (world.getName().equals("world_nether")) {
                    addStructureToLists(player, Structure.BASTION_REMNANT, "Bastion", structureList,
                            locationList);

                    if (GameState.firstNetherEntry != null) {
                        structureList.add("Nether Portal");
                        locationList.add(GameState.firstNetherEntry);
                    }
                }

                // Locate a Ruined Portal in both dimensions.
                addStructureTypeToLists(player, StructureType.RUINED_PORTAL, "Ruined Portal", structureList,
                        locationList);

                // Display the last death location in both dimensions.
                if (PlayerDeathListener.deathLocation.containsKey(player)) {
                    structureList.add("Last Death");
                    locationList.add(PlayerDeathListener.deathLocation.get(player));
                }

                index++;    // switch the located structure

                if (index > locationList.size() - 1)
                    index = 0;

                playerTracksName.put(player, structureList.get(index));
                playerTracksLocation.put(player, locationList.get(index));
                player.sendMessage(Prefixes.getTrackingPrefix() + ChatColor.GRAY + "Tracking "
                        + HexColor.YELLOW + playerTracksName.get(player) + ChatColor.GRAY + ".");

                playerTracksIndex.replace(player, index);
            }
        }
    }

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        String trackedObject = playerTracksName.get(player);
        Location trackedLocation = playerTracksLocation.get(player);

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        String trackedObjectName;
        int distance;

        if (item.getItemMeta().getDisplayName().contains("Tracker")) {
            try {
                if (trackedObject == null) {
                    if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder("PLAYER").color(ChatColor.WHITE).bold(true)
                                        .append(" (Left-Click)").color(ChatColor.GRAY).bold(false).create());
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder("PLAYER").color(ChatColor.WHITE).bold(true)
                                        .append(" (Left-Click)").color(ChatColor.GRAY).bold(false)
                                        .append(" | ").color(ChatColor.DARK_GRAY)
                                        .append("STRUCTURE").color(HexColor.YELLOW).bold(true)
                                        .append(" (Right-Click)").color(ChatColor.GRAY).bold(false).create());
                    }

                    return;
                }

                distance = (int) player.getLocation().distance(trackedLocation);

                // do not show "In close proximity" when tracking a player
                // but still needed to make it easier to find structures since the coords provided are not very accurate
                if (trackedObject.contains("[P]")) {
                    trackedObjectName = trackedObject.substring(4).toUpperCase();

                    // Update the distance to other players constantly since they are moving
                    trackedLocation = Objects.requireNonNull(
                            Bukkit.getPlayer(trackedObject.substring(4))).getLocation();

                    // Calculate new distance
                    distance = (int) player.getLocation().distance(trackedLocation);

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder(trackedObjectName).color(ChatColor.WHITE).bold(true)
                                    .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                    .append(distance + " Blocks away").color(ChatColor.GRAY).create());
                } else {
                    trackedObjectName = trackedObject.toUpperCase();

                    if (distance < 50 && !trackedObjectName.contains("LAST DEATH")) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder(trackedObjectName).color(HexColor.YELLOW).bold(true)
                                        .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                        .append("In close proximity").color(HexColor.GREEN).create());
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder(trackedObjectName).color(HexColor.YELLOW).bold(true)
                                        .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                        .append(distance + " Blocks away").color(ChatColor.GRAY).create());
                    }
                }

                // set the compass target (where to point the needle)
                CompassMeta meta = (CompassMeta) player.getInventory().getItemInMainHand().getItemMeta();

                if (meta != null) {
                    meta.setLodestone(trackedLocation);
                    meta.setLodestoneTracked(false); // no real lodestone present at that location
                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                }

            } catch (IllegalArgumentException ex) {
                // Different dimension (can only be a player).
                if (trackedObject != null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder(trackedObject.substring(4).toUpperCase())
                                    .color(ChatColor.WHITE).bold(true)
                                    .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                    .append(DbValues.getCurrentWorld(trackedLocation)).color(HexColor.RED).create());
                }
            }
        }
    }

    protected void run() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendActionBar(player);
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    /**
     * Finds a specific structure and adds its name and location to the appropriate lists.
     */
    protected void addStructureToLists(
            Player player, Structure structure, String structureName,
            LinkedList<String> structureList, LinkedList<Location> locationList) {
        StructureSearchResult structureSearchResult;

        // Params: player location, structure to search for, radius in chunks, search for unlooted only
        structureSearchResult = player.getWorld().locateNearestStructure(
                player.getLocation(), structure, 16, false);

        if (structureSearchResult != null) {

            // Check if the located structure is outside the Overworld border
            Location center = Objects.requireNonNull(Bukkit.getWorld("world")).getWorldBorder().getCenter();
            if ((center.getX() + 1000) < structureSearchResult.getLocation().getX()
                    || (center.getX() - 1000) > structureSearchResult.getLocation().getX()
                    || (center.getZ() + 1000) < structureSearchResult.getLocation().getZ()
                    || (center.getZ() - 1000) > structureSearchResult.getLocation().getZ()) {
                return;
            }

            structureList.add(structureName);
            locationList.add(structureSearchResult.getLocation());
        }
    }

    /**
     * Finds a specific structure type and adds its name and location to the appropriate lists.
     */
    protected void addStructureTypeToLists(
            Player player, StructureType structureType, String structureName,
            LinkedList<String> structureList, LinkedList<Location> locationList) {
        StructureSearchResult structureSearchResult;

        structureSearchResult = player.getWorld().locateNearestStructure(
                player.getLocation(), structureType, 32, false);
        if (structureSearchResult != null) {
            structureList.add(structureName);
            locationList.add(structureSearchResult.getLocation());
        }
    }

    public ItemStack createTracker() {
        ItemStack tracker = new ItemStack(Material.COMPASS);
        tracker.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = tracker.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Tracker");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Displays the distance towards different",
                ChatColor.GRAY + "players and structures.",
                ChatColor.GRAY + "The compass needle points to the",
                ChatColor.GRAY + "tracked location.",
                "",
                ChatColor.GRAY + "Left-Click: " + ChatColor.WHITE + "Track Player",
                ChatColor.GRAY + "Right-Click: " + HexColor.YELLOW + "Track Structure"));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        tracker.setItemMeta(meta);

        return tracker;
    }
}
