package com.tharox.dragonhunt.enchanting;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Statement;

public class EnchantingListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().contains("Enchant"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String enchantment = item.getItemMeta().getDisplayName();

        // event.getInventory() ... top inventory = enchant
        // if the clicked inventory is not the enchant inventory, cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) return;

        // ERROR: enchantment already bought
        if (enchantment.contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You have already bought this enchantment!");
            return;
        }

        // ERROR: enchantment level not unlocked
        if (enchantment.contains("ʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "Purchase the previous level to unlock this one!");
            return;
        }

        // ERROR: not enough points to purchase the enchantment
        if (Coins.getCoins(player) < EnchantingInventory.enchantmentCost.get(enchantment)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You don't have enough coins! Need "
                    + HexColor.RED + (EnchantingInventory.enchantmentCost.get(enchantment) - Coins.getCoins(player))
                    + ChatColor.RED + " more.");
            return;
        }

        // SUCCESS: enchantment can be purchased - raise the level by 1
        if (enchantment.contains("Sharpness")) {
            Enchantments.Sharpness.put(player, Enchantments.Sharpness.get(player) + 1);
        } else if (enchantment.contains("Fire Aspect")) {
            Enchantments.FireAspect.put(player, Enchantments.FireAspect.get(player) + 1);
        } else if (enchantment.contains("Power")) {
            Enchantments.Power.put(player, Enchantments.Power.get(player) + 1);
        } else if (enchantment.contains("Punch")) {
            Enchantments.Punch.put(player, Enchantments.Punch.get(player) + 1);
        } else if (enchantment.contains("Flame")) {
            Enchantments.Flame.put(player, Enchantments.Flame.get(player) + 1);
        } else if (enchantment.contains("Multishot")) {
            Enchantments.Multishot.put(player, Enchantments.Multishot.get(player) + 1);
        } else if (enchantment.contains("Protection")) {
            Enchantments.Protection.put(player, Enchantments.Protection.get(player) + 1);
        } else if (enchantment.contains("Thorns")) {
            Enchantments.Thorns.put(player, Enchantments.Thorns.get(player) + 1);
        } else if (enchantment.contains("Feather Falling")) {
            Enchantments.FeatherFalling.put(player, Enchantments.FeatherFalling.get(player) + 1);
        }

        purchaseEnchantment(player, enchantment);
    }

    /**
     * @param player player who purchased an enchantment
     */
    private void purchaseEnchantment(Player player, String enchantment) {
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        Coins.removeCoins(player, EnchantingInventory.enchantmentCost.get(enchantment), enchantment);

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            // Substring 4 from enchantment to remove formatting
            String query = "INSERT INTO Purchases (GameID, Player, purchasedObject, coinsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + enchantment.substring(4)
                    + "', '" + (Coins.getCoins(player) + EnchantingInventory.enchantmentCost.get(enchantment))
                    + "', '" + EnchantingInventory.enchantmentCost.get(enchantment)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        // inventory needs to be re-opened (refresh the amount of coins the player has)
        EnchantingInventory inventory = new EnchantingInventory(Main.getInstance().getConfig(), player);
        player.openInventory(inventory.enchant);
    }
}
