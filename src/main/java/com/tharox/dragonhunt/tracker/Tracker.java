package com.tharox.dragonhunt.tracker;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.positions.PositionCommand;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

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

        LinkedList<String> structureList = new LinkedList<>();
        LinkedList<Location> locationList = new LinkedList<>();
        addStructureLocationsToLists(player, structureList, locationList);

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(ChatColor.RED + "Tracker disabled in the End.");
                    return;
                }

                if (!PositionCommand.positions.containsKey(player)) { // 0 positions added
                    player.setSneaking(false);
                }

                if (player.isSneaking()) {
                    index++;    // switch the located position
                    if (index > PositionCommand.positions.get(player).size() - 1) {
                        index = 0;
                    }

                    playerTracksName.put(player, "[POS] " + PositionCommand.positions.get(player).get(index).getName());
                    playerTracksLocation.put(player, PositionCommand.positions.get(player).get(index).getLocation());
                } else {
                    index++;    // switch the located structure
                    if (index > locationList.size() - 1) {
                        index = 0;
                    }

                    playerTracksName.put(player, structureList.get(index));
                    playerTracksLocation.put(player, locationList.get(index));
                }

                playerTracksIndex.replace(player, index);

                player.sendMessage(ChatColor.WHITE + "Tracking " + ChatColor.GOLD.toString()
                        + ChatColor.BOLD + playerTracksName.get(player).toUpperCase() + ChatColor.WHITE + ".");
            }
        }

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {
                player.performCommand("upgrades");
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
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                            ChatColor.RED.toString() + ChatColor.BOLD + "DISABLED IN THE END"));
                    return;
                }

                if (trackedObject == null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                            ChatColor.GREEN.toString() + ChatColor.BOLD
                                    + "RIGHT-CLICK: " + ChatColor.WHITE + "Structure "
                                    + ChatColor.GREEN.toString() + ChatColor.BOLD
                                    + "SHIFT + RIGHT-CLICK: " + ChatColor.WHITE + "Position"));
                    return;
                }

                trackedObjectName = ChatColor.GOLD.toString() + ChatColor.BOLD + trackedObject.toUpperCase()
                        + ChatColor.DARK_GRAY + " » ";
                distance = (int) player.getLocation().distance(trackedLocation);

                if (distance < 100) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedObjectName
                            + ChatColor.GREEN + "In close proximity"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedObjectName
                            + ChatColor.WHITE + distance + " Blocks away"));
                }

                // set the compass target (where to point the needle)
                if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    CompassMeta meta = (CompassMeta) player.getInventory().getItemInMainHand().getItemMeta();

                    if (meta != null) {
                        meta.setLodestone(trackedLocation);
                        meta.setLodestoneTracked(false); // no real lodestone present at that location
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                    }

                } else {
                    player.setCompassTarget(trackedLocation);
                }

            } catch (IllegalArgumentException ex) {
                if (trackedObject != null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedObjectName
                            + ChatColor.RED + DbValues.getCurrentWorld(trackedLocation).toUpperCase()));
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

    protected void addStructureLocationsToLists(
            Player player, LinkedList<String> structureList, LinkedList<Location> locationList) {
        World world = player.getWorld();

        if (world.getName().equals("world")) {
            structureList.add("Village");
            locationList.add(world.locateNearestStructure(player.getLocation(),
                    StructureType.VILLAGE, 32, false));

            structureList.add("Shipwreck");
            locationList.add(world.locateNearestStructure(player.getLocation(),
                    StructureType.SHIPWRECK, 32, false));

        } else if (world.getName().equals("world_nether")) {
            structureList.add("Fortress");
            locationList.add(world.locateNearestStructure(player.getLocation(),
                    StructureType.NETHER_FORTRESS, 32, false));

            structureList.add("Bastion");
            locationList.add(world.locateNearestStructure(player.getLocation(),
                    StructureType.BASTION_REMNANT, 32, false));

            if (GameState.firstNetherEntry != null) {
                structureList.add("Nether Portal");
                locationList.add(GameState.firstNetherEntry);
            }
        }

        // locate a Ruined Portal in both dimensions
        structureList.add("Ruined Portal");
        locationList.add(world.locateNearestStructure(player.getLocation(),
                StructureType.RUINED_PORTAL, 32, false));
    }

    public ItemStack givePlayerTracker() {
        ItemStack tracker = new ItemStack(Material.COMPASS);
        tracker.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = tracker.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker");
        meta.setLore(Arrays.asList(
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Displays the distance towards different structures.",
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "The compass needle points to the",
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "tracked structure location.",
                "",
                ChatColor.WHITE + "Left-Click: " + ChatColor.GREEN + "Open Upgrades menu (/upgrades)",
                ChatColor.WHITE + "Right-Click: " + ChatColor.GREEN + "Switch tracked structure",
                ChatColor.WHITE + "Shift + Right-Click: " + ChatColor.GREEN + "Switch tracked position"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        tracker.setItemMeta(meta);

        return tracker;
    }
}
