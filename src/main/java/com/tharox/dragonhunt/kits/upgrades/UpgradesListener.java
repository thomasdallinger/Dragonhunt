package com.tharox.dragonhunt.kits.upgrades;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Statement;

public class UpgradesListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().contains("UPGRADES"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (item.getItemMeta().getLore() == null) return;

        String upgrade = item.getItemMeta().getDisplayName();
        // Remove the gray color from the lore (7 = gray = number)
        String lore = item.getItemMeta().getLore().get(0).substring(2);
        int value = getValueFromLore(lore);

        // event.getInventory() ... top inventory = upgrades
        // if the clicked inventory is not the upgrades inventory, cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) return;

        // ERROR: upgrade already bought
        if (upgrade.contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You have already purchased this upgrade!");
            return;
        }

        // ERROR: upgrade not unlocked
        if (upgrade.contains("ʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "Purchase the previous level to unlock this one!");
            return;
        }

        // ERROR: not enough points to purchase the upgrade
        if (Coins.getCoins(player) < UpgradesInventory.upgradeCost.get(upgrade)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You do not have enough ⛃! Need "
                    + HexColor.RED + (UpgradesInventory.upgradeCost.get(upgrade) - Coins.getCoins(player))
                    + ChatColor.RED + " more.");
            return;
        }

        // SUCCESS: upgrade can be purchased - increase the value
        if (lore.contains("Cooldown")) {
            Upgrades.Cooldown.put(player, value);
        } else if (lore.contains("Duration")) {
            Upgrades.Duration.put(player, value);
        } else if (lore.contains("Effectiveness")) {
            Upgrades.Effectiveness.put(player, value);
        }

        purchaseUpgrade(player, upgrade);
    }

    /**
     * @param player player who purchased an upgrade
     */
    private void purchaseUpgrade(Player player, String upgrade) {
        player.sendMessage(Prefixes.getCoinPrefix(-UpgradesInventory.upgradeCost.get(upgrade))
                + ChatColor.GRAY + "Purchased " + HexColor.YELLOW + upgrade.substring(16)
                + ChatColor.GRAY + ".");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
        Coins.removeCoins(player, UpgradesInventory.upgradeCost.get(upgrade));

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            // Substring 16 from upgrade to remove formatting
            String query = "INSERT INTO purchases (game_id, player, purchased_item, coins_before, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getUniqueId()
                    + "', '" + upgrade.substring(16)
                    + "', '" + (Coins.getCoins(player) + UpgradesInventory.upgradeCost.get(upgrade))
                    + "', '" + UpgradesInventory.upgradeCost.get(upgrade)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });

        // inventory needs to be re-opened (refresh the amount of coins the player has)
        UpgradesInventory inventory = new UpgradesInventory(Main.getInstance().getConfig(), player);
        player.openInventory(inventory.upgrades);
    }

    private int getValueFromLore(String lore) {
        // Use a regex to get the first number of the string.
        String stringNumber = lore.replaceAll("[^0-9]", "");
        // Convert the string number into an integer.
        if (!stringNumber.equals("")) {
            return Integer.parseInt(stringNumber);
        }

        return 0;
    }
}
