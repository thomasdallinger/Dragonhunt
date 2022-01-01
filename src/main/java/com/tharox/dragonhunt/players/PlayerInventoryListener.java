package com.tharox.dragonhunt.players;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item;

        if (event.getAction().name().contains("HOTBAR") && event.getHotbarButton() != -1) {
            item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
        } else {
            item = event.getCurrentItem();
        }

        if (event.getHotbarButton() == 7 || event.getHotbarButton() == 8) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER) return;

        if (item == null) return;
        if (item.getItemMeta() == null) return;

        //Tracker or Kit ability
        if (event.getSlot() == 7 || event.getSlot() == 8) {
            event.setCancelled(true);
            return;
        }

        //Elytra
        if (item.getType().equals(Material.ELYTRA)) {
            if (event.getSlot() == 38) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getItemMeta() == null) return;

        String displayName = item.getItemMeta().getDisplayName();

        if (displayName.contains(ChatColor.LIGHT_PURPLE + "Kit Ability") || displayName.contains(ChatColor.GOLD + event.getPlayer().getName() + "'s Tracker"))
            event.setCancelled(true);

        if(item.getType() == Material.ELYTRA) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerSwapHandItemsEvent event) {
        ItemStack offHandItem = event.getOffHandItem();

        if (offHandItem == null) return;
        if (offHandItem.getItemMeta() == null) return;

        String displayName = offHandItem.getItemMeta().getDisplayName();
        if (displayName.contains(ChatColor.LIGHT_PURPLE + "Kit Ability") || displayName.contains(ChatColor.GOLD + event.getPlayer().getName() + "'s Tracker")) {
            event.setCancelled(true);
        }
    }
}

