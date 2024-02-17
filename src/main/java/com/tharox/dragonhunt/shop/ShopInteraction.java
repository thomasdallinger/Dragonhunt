package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ShopInteraction implements Listener {
    public ShopInteraction() {
        run();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        World world = player.getWorld();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Shop")) {
                event.setCancelled(true); // Avoid block placement.
                player.performCommand("scout");
            }

            return; // if it was Left-Click, no need to check Right-Click
        }

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Shop")) {
                event.setCancelled(true); // Avoid block placement.
                player.performCommand("shop");
            }
        }
    }

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        if (item.getItemMeta().getDisplayName().contains("Shop")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new ComponentBuilder("SCOUT").color(HexColor.YELLOW).bold(true)
                            .append(" (Left-Click)").color(ChatColor.GRAY).bold(false)
                            .append(" | ").color(ChatColor.DARK_GRAY)
                            .append("SHOP").color(HexColor.YELLOW).bold(true)
                            .append(" (Right-Click)").color(ChatColor.GRAY).bold(false).create());
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
}
