package com.tharox.dragonhunt.tracker;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DbValues;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

/**
 * Tracker functionality
 * Tracker item
 * new Tracker in main - getTracker(), 1 instance for all players
 */
public class Tracker implements Listener {

    public Map<Player, Player> playerTracksPlayer = new HashMap<>();
    public Map<Player, Integer> playerTracksIndex = new HashMap<>();

    public Tracker() {

        run();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        int index = playerTracksIndex.getOrDefault(player, 0);

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        LinkedList<Player> playerList = new LinkedList<>(getServer().getOnlinePlayers());
        List<Player> allOtherPlayers = playerList.stream().filter(o -> !(o.getName().equals(player.getName()))).collect(Collectors.toList());

        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + player.getName() + "'s Tracker")) {

                if (Main.getInstance().getTimer().getTime() < 60) {
                    player.sendMessage(ChatColor.RED + "Tracker disabled for the first minute.");
                    return;
                }

                index++;    //Player swap
                if (index > allOtherPlayers.size() - 1) {
                    index = 0;
                }

                playerTracksPlayer.put(player, allOtherPlayers.get(index));
                playerTracksIndex.replace(player, index);

                player.sendMessage(ChatColor.WHITE + "Tracking " + ChatColor.GOLD.toString()
                        + ChatColor.BOLD + playerTracksPlayer.get(player).getName().toUpperCase() + ChatColor.WHITE + ".");
            }
        }

        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + player.getName() + "'s Tracker")) {
                player.performCommand("gear");
            }
        }
    }

    public void sendActionBar(Player player) {

        ItemStack item = player.getInventory().getItemInMainHand();
        Player trackedPlayer = playerTracksPlayer.get(player);

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        String trackedPlayerName = "";
        int distance;

        if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + player.getName() + "'s Tracker")) {
            try {

                if (Main.getInstance().getTimer().getTime() < 60) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED.toString() + ChatColor.BOLD
                            + "WAITING FOR SIGNAL"));
                    return;
                }

                if (trackedPlayer == null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN.toString() + ChatColor.BOLD
                            + "RIGHT-CLICK " + ChatColor.WHITE + "to track a player"));
                    return;
                }

                trackedPlayerName = ChatColor.GOLD.toString() + ChatColor.BOLD + trackedPlayer.getName().toUpperCase() + ChatColor.DARK_GRAY + " » ";
                distance = (int) player.getLocation().distance(trackedPlayer.getLocation());

                if (distance < 100 && player.getWorld().getName().equals("world")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedPlayerName
                            + ChatColor.RED + "<100 Blocks away"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedPlayerName
                            + ChatColor.WHITE + distance + " Blocks away"));
                }

                //SET TARGET
                if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    CompassMeta meta = (CompassMeta) player.getInventory().getItemInMainHand().getItemMeta();

                    if (meta != null) {
                        meta.setLodestone(trackedPlayer.getLocation());
                        meta.setLodestoneTracked(false); //no real lodestone present at that location
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                    }

                } else {
                    player.setCompassTarget(trackedPlayer.getLocation());
                }

            } catch (IllegalArgumentException ex) {
                if (trackedPlayer != null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(trackedPlayerName
                            + ChatColor.RED + "Different Dimension: "
                            + ChatColor.RED + ChatColor.BOLD + DbValues.getCurrentWorld(trackedPlayer).toUpperCase()));
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

    public ItemStack givePlayerTracker(Player player) {
        ItemStack tracker = new ItemStack(Material.COMPASS);
        tracker.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = tracker.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.GOLD + player.getName() + "'s Tracker");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Left-Click: " + ChatColor.YELLOW + "Open gear upgrades (/gear)",
                ChatColor.WHITE + "Right-Click: " + ChatColor.YELLOW + "Switch tracked player"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        tracker.setItemMeta(meta);

        return tracker;
    }
}
