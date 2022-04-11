package com.tharox.dragonhunt.kitupgrades;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.points.Points;
import com.tharox.dragonhunt.shop.*;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class KitUpgradeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().contains(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "KIT UPGRADES"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String itemName = item.getItemMeta().getDisplayName();

        //event.getInventory() ... top inventory = shop
        //if the clicked inventory is not the shop then cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        //Not enough points to purchase Upgrade level
        if (Points.getPoints(player) < KitUpgradeInventory.KitUpgradeCost.get(itemName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "You don't have enough points! Need "
                    + (KitUpgradeInventory.KitUpgradeCost.get(itemName) - Points.getPoints(player)) + " more.");
            return;
        }

        //Upgrade level not unlocked
        if (itemName.contains("LOCKED")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "Purchase the previous level to unlock this one!");
            return;
        }

        new KitUpgrades(player, itemName, item.getAmount());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Points.removePoints(player, KitUpgradeInventory.KitUpgradeCost.get(itemName));
        player.closeInventory();
        KitUpgradeCommand.openInventory(player);

        addShopAccess(player, itemName);
    }

    //TODO: change later
    private void addShopAccess(Player player, String itemName) {
        /*DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO ShopAccess (GameID, Player, item, pointsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + itemName + "', '"
                    + Points.getPoints(player) + "', '" + ShopInventory.ShopItemCost.get(itemName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });*/
    }

}
