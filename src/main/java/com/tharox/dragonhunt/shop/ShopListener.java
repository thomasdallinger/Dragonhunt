package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.grappler.Grappler;
import com.tharox.dragonhunt.mobility.DoubleJump;
import com.tharox.dragonhunt.upgrades.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    /**
     * Note: Object refers to an upgrade or mobility command.
     */
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

        String objectName = item.getItemMeta().getDisplayName();

        // inventory is shop menu
        if (event.getView().getTitle().contains(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "SHOP MENU")) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            menuNavigation(player, objectName);
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
        if (objectName.equals(HexColor.RED + "Back To Menu")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            player.closeInventory();
            player.openInventory(shopInventory.menu);
            return;
        }

        // upgrade already bought
        if (objectName.contains("UNLOCKED")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You have already bought this upgrade!");
            return;
        }

        // upgrade level not unlocked
        if (objectName.contains("LOCKED")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY
                    + "Purchase the previous level to unlock this one!");
            return;
        }

        // not enough points to purchase the object
        if (Coins.getCoins(player) < ShopInventory.objectCost.get(objectName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You don't have enough coins! Need "
                    + HexColor.RED + (ShopInventory.objectCost.get(objectName) - Coins.getCoins(player))
                    + ChatColor.GRAY + " more.");
            return;
        }

        // upgrade or mobility command can be purchased - raise the level by 1
        if (objectName.contains("Sharpness")) {
            EnchantmentUpgrades.Sharpness.put(player, EnchantmentUpgrades.Sharpness.get(player) + 1);
            purchaseUpgrade(player, objectName, "Weapon");
        } else if (objectName.contains("Knockback")) {
            EnchantmentUpgrades.Knockback.put(player, EnchantmentUpgrades.Knockback.get(player) + 1);
            purchaseUpgrade(player, objectName, "Weapon");
        } else if (objectName.contains("Fire Aspect")) {
            EnchantmentUpgrades.FireAspect.put(player, EnchantmentUpgrades.FireAspect.get(player) + 1);
            purchaseUpgrade(player, objectName, "Weapon");
        } else if (objectName.contains("Power")) {
            EnchantmentUpgrades.Power.put(player, EnchantmentUpgrades.Power.get(player) + 1);
            purchaseUpgrade(player, objectName, "Bow");
        } else if (objectName.contains("Punch")) {
            EnchantmentUpgrades.Punch.put(player, EnchantmentUpgrades.Punch.get(player) + 1);
            purchaseUpgrade(player, objectName, "Bow");
        } else if (objectName.contains("Flame")) {
            EnchantmentUpgrades.Flame.put(player, EnchantmentUpgrades.Flame.get(player) + 1);
            purchaseUpgrade(player, objectName, "Bow");
        } else if (objectName.contains("/spec") || objectName.contains("/top") || objectName.contains("/respawn")) {
            player.closeInventory();
            // coins are removed when performing the command, not in the ShopListener,
            // because players can directly use the commands without opening the shop
            player.performCommand(objectName.substring(5)); // upgrade name without the color codes and "/"
        } else if (objectName.contains("Double Jump")) {
            DoubleJump.playersAllowedToDoubleJump.add(player.getName());
            purchaseUpgrade(player, objectName, "Mobility");
        } else if (objectName.contains("Grappler Cooldown")) {
            if (!Grappler.grapplerCooldown.containsKey(player)) {
                Grappler.grapplerCooldown.put(player, 120);
            } else {
                Grappler.grapplerCooldown.put(player, 80);
            }

            purchaseUpgrade(player, objectName, "Mobility");
        } else if (objectName.contains("More Arrows") || objectName.contains("Effect Arrows")) {
            new ArrowUpgrade(player, objectName);
            purchaseUpgrade(player, objectName, "Arrow");
        } else if (objectName.contains("Efficiency")) {
            EnchantmentUpgrades.Efficiency.put(player, EnchantmentUpgrades.Efficiency.get(player) + 1);
            purchaseUpgrade(player, objectName, "Tool");
        } else if (objectName.contains("Unbreakable")) {
            EnchantmentUpgrades.Unbreakable.put(player, EnchantmentUpgrades.Unbreakable.get(player) + 1);
            purchaseUpgrade(player, objectName, "Tool");
        } else if (objectName.contains("Protection")) {
            EnchantmentUpgrades.Protection.put(player, EnchantmentUpgrades.Protection.get(player) + 1);
            purchaseUpgrade(player, objectName, "Armor");
        } else if (objectName.contains("Thorns")) {
            EnchantmentUpgrades.Thorns.put(player, EnchantmentUpgrades.Thorns.get(player) + 1);
            purchaseUpgrade(player, objectName, "Armor");
        } else if (objectName.contains("Feather Falling")) {
            EnchantmentUpgrades.FeatherFalling.put(player, EnchantmentUpgrades.FeatherFalling.get(player) + 1);
            purchaseUpgrade(player, objectName, "Armor");
        } else if (objectName.contains("Food Source") || objectName.contains("Food Amount")) {
            new FoodUpgrade(player, objectName, item.getAmount());
            purchaseUpgrade(player, objectName, "Food");
        } else if (objectName.contains("Resource Amount")) {
            new ResourceUpgrade(player, objectName, item.getAmount());
            purchaseUpgrade(player, objectName, "Resource");
        } else if (objectName.contains("More XP")) {
            new XPUpgrade(player);
            purchaseUpgrade(player, objectName, "XP");
        }
    }

    /**
     * @param player    player that purchased an upgrade
     * @param inventory inventory to open (refresh after a short delay)
     */
    private void purchaseUpgrade(Player player, String upgradeName, String inventory) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Coins.removeCoins(player, ShopInventory.objectCost.get(upgradeName), upgradeName);

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
}
