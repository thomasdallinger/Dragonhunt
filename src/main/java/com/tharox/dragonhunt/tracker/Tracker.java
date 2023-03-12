package com.tharox.dragonhunt.tracker;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.commands.PositionCommand;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.timer.Timer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Biome;
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

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        int index = playerTracksIndex.getOrDefault(player, 0);

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        // a list of all structures (and locations) trackable in the world the player is in
        LinkedList<String> structureList = new LinkedList<>();
        LinkedList<Location> locationList = new LinkedList<>();

        if (world.getName().equals("world")) {
            addStructureToLists(player, Structure.ANCIENT_CITY, "Ancient City", structureList,
                    locationList);
            addStructureToLists(player, Structure.MINESHAFT, "Mineshaft", structureList,
                    locationList);

        } else if (world.getName().equals("world_nether")) {
            addStructureToLists(player, Structure.BASTION_REMNANT, "Bastion", structureList,
                    locationList);

            if (GameState.firstNetherEntry != null) {
                structureList.add("Nether Portal");
                locationList.add(GameState.firstNetherEntry);
            }
        }

        // locate a Ruined Portal in the OVERWORLD and the NETHER (Tracker not available in the End)
        addStructureTypeToLists(player, StructureType.RUINED_PORTAL, "Ruined Portal", structureList,
                locationList);

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Tracker")) {

                index++;    // switch the located structure

                if (index > locationList.size() - 1)
                    index = 0;

                playerTracksName.put(player, structureList.get(index));
                playerTracksLocation.put(player, locationList.get(index));
                player.sendMessage(Prefixes.getTrackingPrefix() + ChatColor.GRAY + "Tracking "
                        + ChatColor.GOLD + playerTracksName.get(player) + ChatColor.GRAY + ".");

                playerTracksIndex.replace(player, index);
            }
        }

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Tracker")) {
                LinkedList<Player> playerList = new LinkedList<>(Bukkit.getServer().getOnlinePlayers());
                List<Player> allOtherPlayers = playerList.stream().filter(o -> !(o.getName().equals(player.getName())))
                        .collect(Collectors.toList());

                index++;    // switch the located player

                if (index > allOtherPlayers.size() - 1)
                    index = 0;

                playerTracksName.put(player, "[P] " + allOtherPlayers.get(index).getName());
                playerTracksLocation.put(player, allOtherPlayers.get(index).getLocation());
                player.sendMessage(Prefixes.getTrackingPrefix() + ChatColor.GRAY + "Tracking "
                        + ChatColor.YELLOW + playerTracksName.get(player).substring(4) + ChatColor.GRAY + ".");

                playerTracksIndex.replace(player, index);
            }
        }
    }

    public void sendActionBar(Player player) {
        Timer timer = Main.getInstance().getTimer();
        ItemStack item = player.getInventory().getItemInMainHand();
        String trackedObject = playerTracksName.get(player);
        Location trackedLocation = playerTracksLocation.get(player);

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        String trackedObjectName = "";
        int distance;

        if (item.getItemMeta().getDisplayName().contains("Tracker")) {
            try {
                if (trackedObject == null) {
                    if(timer.getTime() % 2 == 0) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder("Structures").color(ChatColor.GOLD).bold(true)
                                        .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                        .append("Right-Click").color(ChatColor.GRAY).italic(true).create());
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder("Players").color(ChatColor.YELLOW).bold(true)
                                        .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                        .append("Left-Click").color(ChatColor.GRAY).italic(true).create());
                        /*player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new ComponentBuilder("LEFT-CLICK: ").color(ChatColor.WHITE).bold(true)
                                        .append("Player").color(ChatColor.YELLOW).bold(false)
                                        .append(" | ").color(ChatColor.DARK_GRAY)
                                        .append("RIGHT-CLICK: ").color(ChatColor.WHITE).bold(true)
                                        .append("Structure").color(ChatColor.GOLD).bold(false).create());*/
                    }
                    return;
                }

                if (trackedObject.contains("[P]")) {
                    trackedObjectName = ChatColor.YELLOW.toString() + ChatColor.BOLD
                            + trackedObject.substring(4).toUpperCase() + ChatColor.DARK_GRAY + " » ";
                } else {
                    trackedObjectName = ChatColor.GOLD.toString() + ChatColor.BOLD + trackedObject.toUpperCase()
                            + ChatColor.DARK_GRAY + " » ";
                }

                distance = (int) player.getLocation().distance(trackedLocation);

                // do not show "In close proximity" when tracking a position
                // but still needed to make it easier to find structures since the coords provided are not very accurate
                if (distance < 75 && !trackedObject.contains("[P]")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(trackedObjectName)
                            .append("In close proximity").color(HexColor.GREEN).create());
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(trackedObjectName)
                            .append(distance + " Blocks away").color(ChatColor.WHITE).create());
                }

                // set the compass target (where to point the needle)
                CompassMeta meta = (CompassMeta) player.getInventory().getItemInMainHand().getItemMeta();

                if (meta != null) {
                    meta.setLodestone(trackedLocation);
                    meta.setLodestoneTracked(false); // no real lodestone present at that location
                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                }

            } catch (IllegalArgumentException ex) {
                // Different dimension.
                if (trackedObject != null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(trackedObjectName)
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

        structureSearchResult = player.getWorld().locateNearestStructure(
                player.getLocation(), structure, 32, false);
        if (structureSearchResult != null) {
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
                ChatColor.GRAY + "Left-Click: " + ChatColor.YELLOW + "Switch tracked player",
                ChatColor.GRAY + "Right-Click: " + ChatColor.GOLD + "Switch tracked structure"));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        tracker.setItemMeta(meta);

        return tracker;
    }
}
