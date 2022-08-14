package com.tharox.dragonhunt.tracker;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.commands.PositionCommand;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
            addStructureTypeToLists(player, StructureType.SHIPWRECK, "Shipwreck", structureList,
                    locationList);
            addStructureToLists(player, Structure.ANCIENT_CITY, "Ancient City", structureList,
                    locationList);
            addStructureToLists(player, Structure.MINESHAFT, "Mineshaft", structureList,
                    locationList);

        } else if (world.getName().equals("world_nether")) {
            addStructureToLists(player, Structure.FORTRESS, "Fortress", structureList,
                    locationList);
            addStructureToLists(player, Structure.BASTION_REMNANT, "Bastion", structureList,
                    locationList);

            if (GameState.firstNetherEntry != null) {
                structureList.add("Nether Portal");
                locationList.add(GameState.firstNetherEntry);
            }
        }

        // locate a Ruined Portal in the OVERWORLD and the NETHER (Tracker disabled in the End)
        addStructureTypeToLists(player, StructureType.RUINED_PORTAL, "Ruined Portal", structureList,
                locationList);

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "Tracker disabled in the End.");
                    return;
                }

                if (!PositionCommand.positions.containsKey(player)) { // 0 positions added
                    player.setSneaking(false);
                }

                index++;    // switch the located structure / position (if sneaking)

                if (player.isSneaking()) {
                    if (index > PositionCommand.positions.get(player).size() - 1)
                        index = 0;

                    playerTracksName.put(player, "[POS] " + PositionCommand.positions.get(player).get(index).getName());
                    playerTracksLocation.put(player, PositionCommand.positions.get(player).get(index).getLocation());
                    player.sendMessage(Prefixes.getTrackingPrefix() + ChatColor.GRAY + "Tracking "
                            + ChatColor.DARK_GREEN + playerTracksName.get(player).substring(6) + ChatColor.GRAY + ".");
                } else {
                    if (index > locationList.size() - 1)
                        index = 0;

                    playerTracksName.put(player, structureList.get(index));
                    playerTracksLocation.put(player, locationList.get(index));
                    player.sendMessage(Prefixes.getTrackingPrefix() + ChatColor.GRAY + "Tracking "
                            + ChatColor.GOLD + playerTracksName.get(player) + ChatColor.GRAY + ".");
                }

                playerTracksIndex.replace(player, index);
            }
        }

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {
                player.performCommand("shop");
            }
        }
    }

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        String trackedObject = playerTracksName.get(player);
        Location trackedLocation = playerTracksLocation.get(player);

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        String trackedObjectName = "";
        int distance;

        if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {
            try {

                if (player.getWorld().getName().equals("world_the_end")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder("DISABLED IN THE END").color(HexColor.RED).bold(true).create());
                    return;
                }

                if (trackedObject == null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder("RIGHT-CLICK: ").color(ChatColor.GOLD).bold(true)
                                    .append("Structure").color(ChatColor.WHITE).bold(false)
                                    .append(" | ").color(ChatColor.DARK_GRAY)
                                    .append("SHIFT + RIGHT-CLICK: ").color(ChatColor.DARK_GREEN).bold(true)
                                    .append("Position").color(ChatColor.WHITE).bold(false).create());

                    return;
                }

                if (trackedObject.contains("[POS]")) {
                    trackedObjectName = ChatColor.DARK_GREEN.toString() + ChatColor.BOLD
                            + trackedObject.substring(6).toUpperCase() + ChatColor.DARK_GRAY + " » ";
                } else {
                    trackedObjectName = ChatColor.GOLD.toString() + ChatColor.BOLD + trackedObject.toUpperCase()
                            + ChatColor.DARK_GRAY + " » ";
                }

                distance = (int) player.getLocation().distance(trackedLocation);

                // do not show "In close proximity" when tracking a position
                // but still needed to make it easier to find structures since the coords provided are not very accurate
                if (distance < 100 && !trackedObject.contains("[POS]")) {
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

    public ItemStack givePlayerTracker() {
        ItemStack tracker = new ItemStack(Material.COMPASS);
        tracker.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = tracker.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker");
        meta.setLore(Arrays.asList(
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Displays the distance towards different",
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "structures and positions.",
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "The compass needle points to the",
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "tracked structure/position location.",
                "",
                ChatColor.WHITE + "Left-Click: " + HexColor.HIGHLIGHT + "Open shop menu (/shop)",
                ChatColor.WHITE + "Right-Click: " + ChatColor.GOLD + "Switch tracked structure",
                ChatColor.WHITE + "Shift + Right-Click: " + ChatColor.DARK_GREEN + "Switch tracked position"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        tracker.setItemMeta(meta);

        return tracker;
    }
}
