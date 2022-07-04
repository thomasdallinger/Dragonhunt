package com.tharox.dragonhunt.upgradeMenu;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.points.UpgradePoints;
import com.tharox.dragonhunt.upgrades.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Statement;

public class UpgradeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UpgradeInventory upgradeInventory = new UpgradeInventory(Main.getInstance().getConfig(), player);

        if (!event.getView().getTitle().contains(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "UPGRADES"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String upgradeName = item.getItemMeta().getDisplayName();

        // inventory is Upgrade Menu
        if (event.getView().getTitle().contains(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "UPGRADES MENU")) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            menuNavigation(player, upgradeName);
            return;
        }

        // TODO: seems to be not needed
        // item is air (placeholder)
        if (item.getType() == Material.AIR) {
            return;
        }

        // event.getInventory() ... top inventory = upgrades
        // if the clicked inventory is not the upgrades inventory, cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        // back to menu functionality
        if (upgradeName.equals(ChatColor.RED + "Back to menu")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            player.closeInventory();
            player.openInventory(upgradeInventory.menu);
            return;
        }

        // not enough points to purchase upgrade level
        if (UpgradePoints.getPoints(player) < UpgradeInventory.upgradeCost.get(upgradeName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "You don't have enough points! Need "
                    + (UpgradeInventory.upgradeCost.get(upgradeName) - UpgradePoints.getPoints(player)) + " more.");
            return;
        }

        // upgrade already bought
        if (upgradeName.contains("UNLOCKED")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "You have already bought this upgrade!");
            return;
        }

        // upgrade level not unlocked
        if (upgradeName.contains("LOCKED")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "Purchase the previous level to unlock this one!");
            return;
        }

        if (upgradeName.contains("Sharpness") || upgradeName.contains("Knockback") || upgradeName.contains("Fire Aspect")) {
            new WeaponUpgrade(player, upgradeName, item.getAmount());
            purchaseUpgrade(player, upgradeName, "Weapon");
        } else if (upgradeName.contains("Power") || upgradeName.contains("Punch") || upgradeName.contains("Flame")) {
            new BowUpgrade(player, upgradeName, item.getAmount());
            purchaseUpgrade(player, upgradeName, "Bow");
        } else if (upgradeName.contains("Piercing") || upgradeName.contains("Multishot") || upgradeName.contains("Quick Charge")) {
            new CrossbowUpgrade(player, upgradeName, item.getAmount());
            purchaseUpgrade(player, upgradeName, "Crossbow");
        } else if (upgradeName.contains("More Arrows") || upgradeName.contains("Effect Arrows")) {
            new ArrowUpgrade(player, upgradeName);
            purchaseUpgrade(player, upgradeName, "Arrow");
        } else if (upgradeName.contains("Efficiency") || upgradeName.contains("Unbreakable")) {
            new ToolUpgrade(player, upgradeName, item.getAmount());
            purchaseUpgrade(player, upgradeName, "Tool");
        } else if (upgradeName.contains("Protection") || upgradeName.contains("Thorns") || upgradeName.contains("Feather Falling")
                || upgradeName.contains("Depth Strider") || upgradeName.contains("Respiration") || upgradeName.contains("Aqua Affinity")) {
            new ArmorUpgrade(player, upgradeName, item.getAmount());
            purchaseUpgrade(player, upgradeName, "Armor");
        } else if (upgradeName.contains("Food Source") || upgradeName.contains("Food Amount")) {
            new FoodUpgrade(player, upgradeName, item.getAmount());
            purchaseUpgrade(player, upgradeName, "Food");
        } else if (upgradeName.contains("Resource Amount")) {
            new ResourceUpgrade(player, upgradeName, item.getAmount());
            purchaseUpgrade(player, upgradeName, "Resource");
        } else if (upgradeName.contains("More XP")) {
            new XPUpgrade(player);
            purchaseUpgrade(player, upgradeName, "XP");
        }

        addUpgradeAccess(player, upgradeName);
    }

    /**
     * @param player    player that purchased an upgrade
     * @param inventory inventory to open (refresh after a short delay)
     */
    private void purchaseUpgrade(Player player, String upgradeName, String inventory) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        UpgradePoints.removePoints(player, UpgradeInventory.upgradeCost.get(upgradeName));

        menuNavigation(player, inventory);
    }

    private void menuNavigation(Player player, String name) {
        UpgradeInventory upgradeInventory = new UpgradeInventory(Main.getInstance().getConfig(), player);

        if (name.contains("Weapon"))
            player.openInventory(upgradeInventory.weapon);
        else if (name.contains("Bow"))
            player.openInventory(upgradeInventory.bow);
        else if (name.contains("Crossbow"))
            player.openInventory(upgradeInventory.crossbow);
        else if (name.contains("Arrow"))
            player.openInventory(upgradeInventory.arrow);
        else if (name.contains("Tool"))
            player.openInventory(upgradeInventory.tool);
        else if (name.contains("Armor"))
            player.openInventory(upgradeInventory.armor);
        else if (name.contains("Food"))
            player.openInventory(upgradeInventory.food);
        else if (name.contains("Resource"))
            player.openInventory(upgradeInventory.resource);
        else if (name.contains("XP"))
            player.openInventory(upgradeInventory.xp);
    }

    private void addUpgradeAccess(Player player, String upgradeName) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO UpgradeAccess (GameID, Player, upgrade, pointsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + upgradeName.substring(4) + "', '"
                    + UpgradePoints.getPoints(player) + "', '" + UpgradeInventory.upgradeCost.get(upgradeName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}