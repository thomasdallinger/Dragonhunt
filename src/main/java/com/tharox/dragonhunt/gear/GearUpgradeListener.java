package com.tharox.dragonhunt.gear;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
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

public class GearUpgradeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GearUpgradeInventory gearUpgradeInventory = new GearUpgradeInventory(Main.getInstance().getConfig(), player);

        if (!event.getView().getTitle().contains(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "GEAR UPGRADES"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String itemName = item.getItemMeta().getDisplayName();

        //Inventory is Gear Upgrade Menu
        if (event.getView().getTitle().contains(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "GEAR UPGRADES MENU")) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            menuNavigation(player, itemName);
            return;
        }

        //TODO: seems to be not needed
        //item is air (placeholder)
        if (item.getType() == Material.AIR) {
            return;
        }

        //event.getInventory() ... top inventory = gear upgrades
        //if the clicked inventory is not the gear upgrades inventory, cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        //Back to menu functionality
        if (itemName.equals(ChatColor.RED + "Back to menu")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            player.closeInventory();
            player.openInventory(gearUpgradeInventory.menu);
            return;
        }

        //Not enough points to purchase Upgrade level
        if (Points.getPoints(player) < GearUpgradeInventory.upgradeCost.get(itemName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "You don't have enough points! Need "
                    + (GearUpgradeInventory.upgradeCost.get(itemName) - Points.getPoints(player)) + " more.");
            return;
        }

        //Upgrade level not unlocked
        if (itemName.contains("LOCKED")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "Purchase the previous level to unlock this one!");
            return;
        }

        if (itemName.contains("Sharpness") || itemName.contains("Knockback") || itemName.contains("Fire Aspect")) {
            new WeaponUpgrade(player, itemName, item.getAmount());
            purchaseUpgrade(player, itemName, "Weapon");
        } else if (itemName.contains("Power") || itemName.contains("Punch") || itemName.contains("Flame")) {
            new BowUpgrade(player, itemName, item.getAmount());
            purchaseUpgrade(player, itemName, "Bow");
        } else if (itemName.contains("Piercing") || itemName.contains("Multishot") || itemName.contains("Quick Charge")) {
            new CrossbowUpgrade(player, itemName, item.getAmount());
            purchaseUpgrade(player, itemName, "Crossbow");
        } else if (itemName.contains("More Arrows") || itemName.contains("Effect Arrows")) {
            new ArrowUpgrade(player, itemName);
            purchaseUpgrade(player, itemName, "Arrow");
        } else if (itemName.contains("Efficiency") || itemName.contains("Unbreaking")) {
            new ToolUpgrade(player, itemName, item.getAmount());
            purchaseUpgrade(player, itemName, "Tool");
        } else if (itemName.contains("Protection") || itemName.contains("Thorns") || itemName.contains("Feather Falling")
                || itemName.contains("Depth Strider") || itemName.contains("Respiration") || itemName.contains("Aqua Affinity")) {
            new ArmorUpgrade(player, itemName, item.getAmount());
            purchaseUpgrade(player, itemName, "Armor");
        } else if (itemName.contains("Food Source") || itemName.contains("Food Amount")) {
            new FoodUpgrade(player, itemName, item.getAmount());
            purchaseUpgrade(player, itemName, "Food");
        }

        addGearUpgradeAccess(player, itemName);
    }

    /**
     * @param player    player that purchased an upgrade
     * @param inventory inventory to open (refresh after a short delay)
     */
    private void purchaseUpgrade(Player player, String itemName, String inventory) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Points.removePoints(player, GearUpgradeInventory.upgradeCost.get(itemName));

        menuNavigation(player, inventory);
    }

    private void menuNavigation(Player player, String name) {
        GearUpgradeInventory gearUpgradeInventory = new GearUpgradeInventory(Main.getInstance().getConfig(), player);

        if (name.contains("Weapon"))
            player.openInventory(gearUpgradeInventory.weapon);
        else if (name.contains("Bow"))
            player.openInventory(gearUpgradeInventory.bow);
        else if (name.contains("Crossbow"))
            player.openInventory(gearUpgradeInventory.crossbow);
        else if (name.contains("Arrow"))
            player.openInventory(gearUpgradeInventory.arrow);
        else if (name.contains("Tool"))
            player.openInventory(gearUpgradeInventory.tool);
        else if (name.contains("Armor"))
            player.openInventory(gearUpgradeInventory.armor);
        else if (name.contains("Food"))
            player.openInventory(gearUpgradeInventory.food);
    }

    private void addGearUpgradeAccess(Player player, String itemName) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO ShopAccess (GameID, Player, item, pointsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + itemName + "', '"
                    + Points.getPoints(player) + "', '" + GearUpgradeInventory.upgradeCost.get(itemName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}