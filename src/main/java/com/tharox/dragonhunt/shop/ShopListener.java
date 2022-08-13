package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.upgrades.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Statement;

public class ShopListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ShopInventory shopInventory = new ShopInventory(Main.getInstance().getConfig(), player);

        if (!event.getView().getTitle().contains(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "SHOP"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String upgradeName = item.getItemMeta().getDisplayName();

        // inventory is Upgrade Menu
        if (event.getView().getTitle().contains(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "SHOP MENU")) {
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

        if (event.getClick() == ClickType.NUMBER_KEY) return;

        // back to menu functionality
        if (upgradeName.equals(ChatColor.RED + "Back to menu")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            player.closeInventory();
            player.openInventory(shopInventory.menu);
            return;
        }

        // not enough points to purchase upgrade level
        if (Coins.getCoins(player) < ShopInventory.upgradeCost.get(upgradeName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(ChatColor.RED + "You don't have enough coins! Need "
                    + (ShopInventory.upgradeCost.get(upgradeName) - Coins.getCoins(player)) + " more.");
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

        // upgrade can be purchased - raise the level by 1
        if (upgradeName.contains("Sharpness")) {
            EnchantmentUpgrades.Sharpness.put(player, EnchantmentUpgrades.Sharpness.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Weapon");
        } else if (upgradeName.contains("Knockback")) {
            EnchantmentUpgrades.Knockback.put(player, EnchantmentUpgrades.Knockback.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Weapon");
        } else if (upgradeName.contains("Fire Aspect")) {
            EnchantmentUpgrades.FireAspect.put(player, EnchantmentUpgrades.FireAspect.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Weapon");
        } else if (upgradeName.contains("Power")) {
            EnchantmentUpgrades.Power.put(player, EnchantmentUpgrades.Power.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Bow");
        } else if (upgradeName.contains("Punch")) {
            EnchantmentUpgrades.Punch.put(player, EnchantmentUpgrades.Punch.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Bow");
        } else if (upgradeName.contains("Flame")) {
            EnchantmentUpgrades.Flame.put(player, EnchantmentUpgrades.Flame.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Bow");
        } else if (upgradeName.contains("/spec") || upgradeName.contains("/top") || upgradeName.contains("/respawn")) {
            player.closeInventory();
            Coins.removeCoins(player, ShopInventory.upgradeCost.get(upgradeName));
            player.performCommand(upgradeName.substring(5)); // upgrade name without the color codes and "/"
        } else if (upgradeName.contains("More Arrows") || upgradeName.contains("Effect Arrows")) {
            new ArrowUpgrade(player, upgradeName);
            purchaseUpgrade(player, upgradeName, "Arrow");
        } else if (upgradeName.contains("Efficiency")) {
            EnchantmentUpgrades.Efficiency.put(player, EnchantmentUpgrades.Efficiency.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Tool");
        } else if (upgradeName.contains("Unbreakable")) {
            EnchantmentUpgrades.Unbreakable.put(player, EnchantmentUpgrades.Unbreakable.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Tool");
        } else if (upgradeName.contains("Protection")) {
            EnchantmentUpgrades.Protection.put(player, EnchantmentUpgrades.Protection.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Armor");
        } else if (upgradeName.contains("Thorns")) {
            EnchantmentUpgrades.Thorns.put(player, EnchantmentUpgrades.Thorns.get(player) + 1);
            purchaseUpgrade(player, upgradeName, "Armor");
        } else if (upgradeName.contains("Feather Falling")) {
            EnchantmentUpgrades.FeatherFalling.put(player, EnchantmentUpgrades.FeatherFalling.get(player) + 1);
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
        Coins.removeCoins(player, ShopInventory.upgradeCost.get(upgradeName));

        menuNavigation(player, inventory);
    }

    private void menuNavigation(Player player, String name) {
        ShopInventory shopInventory = new ShopInventory(Main.getInstance().getConfig(), player);

        if (name.contains("Weapon"))
            player.openInventory(shopInventory.weapon);
        else if (name.contains("Bow"))
            player.openInventory(shopInventory.bow);
        else if (name.contains("Mobility"))
            player.openInventory(shopInventory.mobility);
        else if (name.contains("Arrow"))
            player.openInventory(shopInventory.arrow);
        else if (name.contains("Tool"))
            player.openInventory(shopInventory.tool);
        else if (name.contains("Armor"))
            player.openInventory(shopInventory.armor);
        else if (name.contains("Food"))
            player.openInventory(shopInventory.food);
        else if (name.contains("Resource"))
            player.openInventory(shopInventory.resource);
        else if (name.contains("XP"))
            player.openInventory(shopInventory.xp);
    }

    private void addUpgradeAccess(Player player, String upgradeName) {
        DatabaseExecutor.executeDatabaseHandler((con) -> {
            String query = "INSERT INTO UpgradeAccess (GameID, Player, upgrade, pointsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + upgradeName.substring(4) + "', '"
                    + Coins.getCoins(player) + "', '" + ShopInventory.upgradeCost.get(upgradeName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}