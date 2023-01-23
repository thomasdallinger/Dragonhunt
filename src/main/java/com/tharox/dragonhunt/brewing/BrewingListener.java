package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.enchanting.EnchantingInventory;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BrewingListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().contains(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "BREWING STAND"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String itemName = meta.getDisplayName();

        // event.getInventory() ... top inventory = brewing stand
        // if the clicked inventory is not the brewing stand inventory, cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) return;

        // ERROR: not enough points to purchase the item
        if (Coins.getCoins(player) < BrewingInventory.itemCost.get(itemName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You don't have enough coins! Need "
                    + HexColor.RED + (BrewingInventory.itemCost.get(itemName) - Coins.getCoins(player))
                    + ChatColor.GRAY + " more.");
            return;
        }

        // SUCCESS: item can be purchased - remove the items lore (cost, etc.)
        // NOTE: item will be reset in the inventory after purchase (lore will be back in the brewing stand inventory)
        List<String> stringList = new ArrayList<>();
        meta.setLore(stringList);
        item.setItemMeta(meta);

        GameState.putItemInInventory(player, item);
        purchaseItem(player, itemName);
    }

    /**
     * @param player player who purchased an item
     */
    private void purchaseItem(Player player, String enchantment) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Coins.removeCoins(player, BrewingInventory.itemCost.get(enchantment), enchantment);

        // inventory needs to be re-opened (refresh the amount of coins the player has)
        BrewingInventory inventory = new BrewingInventory(Main.getInstance().getConfig(), player);
        player.openInventory(inventory.brewingStand);
    }
}
