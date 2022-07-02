package com.tharox.dragonhunt.tracker;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.timer.Timer;
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

    public Map<Player, String> playerTracksStructure = new HashMap<>();
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

        if (world.getName().equals("world")) {
            locationList.add(world.locateNearestStructure(player.getLocation(), StructureType.VILLAGE, 32, false));
            structureList.add("Village");
            locationList.add(world.locateNearestStructure(player.getLocation(), StructureType.SHIPWRECK, 32, false));
            structureList.add("Shipwreck");
        } else if (world.getName().equals("world_nether")) {
            locationList.add(world.locateNearestStructure(player.getLocation(), StructureType.NETHER_FORTRESS, 32, false));
            structureList.add("Fortress");
            locationList.add(world.locateNearestStructure(player.getLocation(), StructureType.BASTION_REMNANT, 32, false));
            structureList.add("Bastion");
        }

        // locate a Ruined Portal in both dimensions
        locationList.add(world.locateNearestStructure(player.getLocation(), StructureType.RUINED_PORTAL, 32, false));
        structureList.add("Ruined Portal");

        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {

                if (Main.getInstance().getTimer().getTime() < 30) {
                    player.sendMessage(ChatColor.RED + "Tracker currently disabled.");
                    return;
                }

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(ChatColor.RED + "Tracker disabled in the End.");
                    return;
                }

                index++;    // switch the located structure
                if (index > locationList.size() - 1) {
                    index = 0;
                }

                playerTracksStructure.put(player, structureList.get(index));
                playerTracksLocation.put(player, locationList.get(index));
                playerTracksIndex.replace(player, index);

                player.sendMessage(ChatColor.WHITE + "Tracking " + ChatColor.GOLD.toString()
                        + ChatColor.BOLD + playerTracksStructure.get(player).toUpperCase() + ChatColor.WHITE + ".");
            }
        }

        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {
                player.performCommand("gear");
            }
        }
    }

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        String trackedStructure = playerTracksStructure.get(player);
        Location trackedLocation = playerTracksLocation.get(player);
        Timer timer = Main.getInstance().getTimer();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        String trackedStructureName = "";
        int distance;

        if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Tracker")) {
            try {

                if (timer.getTime() < 30) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                            ChatColor.RED.toString() + ChatColor.BOLD + "UNAVAILABLE FOR "
                            + ChatColor.DARK_RED + ChatColor.BOLD + (30 - timer.getTime())
                            + ChatColor.RED + ChatColor.BOLD + " SECONDS"));
                    return;
                }

                if (trackedStructure == null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN.toString() + ChatColor.BOLD
                            + "RIGHT-CLICK " + ChatColor.WHITE + "to track a structure"));
                    return;
                }

                trackedStructureName = ChatColor.GOLD.toString() + ChatColor.BOLD + trackedStructure.toUpperCase() + ChatColor.DARK_GRAY + " » ";
                distance = (int) player.getLocation().distance(trackedLocation);

                if (distance < 100) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedStructureName
                            + ChatColor.GREEN + "In close proximity"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedStructureName
                            + ChatColor.WHITE + distance + " Blocks away"));
                }

                //SET TARGET
                if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    CompassMeta meta = (CompassMeta) player.getInventory().getItemInMainHand().getItemMeta();

                    if (meta != null) {
                        meta.setLodestone(trackedLocation);
                        meta.setLodestoneTracked(false); //no real lodestone present at that location
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                    }

                } else {
                    player.setCompassTarget(trackedLocation);
                }

            } catch (IllegalArgumentException ex) {
                if (trackedStructure != null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedStructureName
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
                ChatColor.WHITE + "Left-Click: " + ChatColor.GREEN + "Open Gear Upgrades (/gear)",
                ChatColor.WHITE + "Right-Click: " + ChatColor.GREEN + "Switch tracked structure"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        tracker.setItemMeta(meta);

        return tracker;
    }
}
