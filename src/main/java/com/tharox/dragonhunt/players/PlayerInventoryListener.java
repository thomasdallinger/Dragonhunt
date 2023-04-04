package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.kits.classes.KitClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
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

        // Tracker = 7, Grappler / END Teleport = 8, Generated Arrows = 17
        if (event.getHotbarButton() == 7 || event.getHotbarButton() == 8
                || event.getHotbarButton() == 17) {
            event.setCancelled(true);
            return;
        }

        // Check if the inventory is not null or an irrelevant one before checking the inventory slots
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER) return;

        if (event.getSlot() == 7 || event.getSlot() == 8 || event.getSlot() == 17) {
            event.setCancelled(true);
            return;
        }

        if (item == null) return;
        if (item.getItemMeta() == null) return;

        // Check if the item was an Elytra and if the Chestplate slot was clicked
        if (item.getType().equals(Material.ELYTRA)) {
            if (event.getSlot() == 38) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        if (event.getView().getTitle().contains("MODIFIERS")) {
            if (GameState.modifierInventoryClose) return;

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(),
                    () -> player.openInventory(inv), 1);
            return;
        }

        if (event.getView().getTitle().contains("PICK A KIT")) {
            if (GameState.kitInventoryClose.get(player)) return;

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(),
                    () -> player.openInventory(inv), 1);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getItemMeta() == null) return;

        String displayName = item.getItemMeta().getDisplayName();

        if (displayName.contains("Respawn Anchor")
                || displayName.contains("Tracker")
                || displayName.contains(KitClass.ASSASSIN)
                || displayName.contains(KitClass.TANK)
                || displayName.contains(KitClass.SUPPORT)
                || displayName.contains(KitClass.MOBILITY)
                || displayName.contains("Enter The End")
                || displayName.contains("Generated Arrow"))
            event.setCancelled(true);

        if (item.getType() == Material.ELYTRA) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        ItemStack offHandItem = event.getOffHandItem();

        if (offHandItem == null) return;
        if (offHandItem.getItemMeta() == null) return;

        String displayName = offHandItem.getItemMeta().getDisplayName();
        if (displayName.contains("Respawn Anchor")
                || displayName.contains("Tracker")
                || displayName.contains(KitClass.ASSASSIN)
                || displayName.contains(KitClass.TANK)
                || displayName.contains(KitClass.SUPPORT)
                || displayName.contains(KitClass.MOBILITY)
                || displayName.contains("Enter The End")) {
            // Generated Arrows can't be swapped: not in the hotbar, no checking needed
            event.setCancelled(true);
        }

        // Elytra cannot be swapped: not in the hotbar, no checking needed
    }
}

