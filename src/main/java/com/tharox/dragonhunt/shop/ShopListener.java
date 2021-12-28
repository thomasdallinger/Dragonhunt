package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.points.Points;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Statement;

public class ShopListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().contains(ChatColor.GOLD.toString() + ChatColor.BOLD + "Shop")) return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String itemName = item.getItemMeta().getDisplayName();

        //Inventory is shop menu
        if (event.getView().getTitle().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Shop Menu")) {
            menuNavigation(player, itemName);
            return;
        }

        //item is sub-category
        if (itemName.contains("»")) {
            return;
        }

        //TODO: seems to be not needed
        //item is air (placeholder)
        if (item.getType() == Material.AIR) {
            return;
        }

        //event.getInventory() ... top inventory = shop
        //if the clicked inventory is not the shop then cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        //Back to menu functionality
        if (itemName.equals(ChatColor.RED + "Back to menu")) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            player.openInventory(ShopInventory.menu);
            return;
        }

        //normal item
        if (Points.getPoints(player) < ShopInventory.ShopItemCost.get(itemName)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You don't have enough points!");
            return;
        }

        if (!GameState.sameItemAlreadyInInventory(player, item)) {
            if (player.getInventory().firstEmpty() == -1) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Clear your Inventory first!");
                return;
            }

            player.getInventory().addItem(item);
        }

        addShopAccess(player, itemName);

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Points.removePoints(player, ShopInventory.ShopItemCost.get(itemName));
    }

    private void menuNavigation(Player player, String name) {
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        if (name.contains("Enchanting")) {
            player.openInventory(ShopInventory.enchanting);
        } else if (name.contains("Food")) {
            player.openInventory(ShopInventory.foodPotionsArrows);
        } else if (name.contains("Blocks")) {
            player.openInventory(ShopInventory.blocksRedstone);
        }
    }

    private void addShopAccess(Player player, String itemName) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO ShopAccess (GameID, Player, item, pointsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + itemName + "', '"
                    + Points.getPoints(player) + "', '" + ShopInventory.ShopItemCost.get(itemName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}