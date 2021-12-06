package com.tharox.dragonhunt.tracker;

import com.tharox.dragonhunt.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

                //only works in the overworld
                if (!player.getWorld().getName().equals("world")) {
                    player.sendMessage(ChatColor.RED + "Tracker disabled in nether & end.");
                    return;
                }

                //less than 2 minutes played
                if (Main.getInstance().getTimer().getTime() < 120) {
                    player.sendMessage(ChatColor.RED + "Tracker disabled for the first 2 minutes.");
                    return;
                }

                index++;    //Player swap
                if (index > allOtherPlayers.size() - 1) {
                    index = 0;
                }

                playerTracksPlayer.put(player, allOtherPlayers.get(index));
                playerTracksIndex.replace(player, index);

                player.sendMessage(ChatColor.WHITE + "Now Tracking: " + ChatColor.RED + playerTracksPlayer.get(player).getName());
            }
        }
    }

    public void sendActionBar(Player player) {

        ItemStack item = player.getInventory().getItemInMainHand();
        Player trackedPlayer = playerTracksPlayer.get(player);

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        int distance;

        if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + player.getName() + "'s Tracker")) {
            try {
                //only works in the overworld
                if (!player.getWorld().getName().equals("world")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "No signal in nether/end.."));
                    return;
                }

                //less than 2 minutes played
                if (Main.getInstance().getTimer().getTime() < 120) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Waiting for signal.."));
                    return;
                }

                if (trackedPlayer == null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Tracking no player.."));
                    return;
                }

                distance = (int) player.getLocation().distance(trackedPlayer.getLocation());

                if (distance < 150) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + trackedPlayer.getName()
                            + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "<150 Blocks away"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + trackedPlayer.getName()
                            + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + distance + " Blocks away"));
                }

                //SET TARGET
                player.setCompassTarget(trackedPlayer.getLocation());

            } catch (IllegalArgumentException ex) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + trackedPlayer.getName()
                        + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Different Dimension"));
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
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        tracker.setItemMeta(meta);

        return tracker;
    }
}
