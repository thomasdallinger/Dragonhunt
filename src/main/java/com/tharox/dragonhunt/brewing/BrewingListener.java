package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Statement;
import java.util.Objects;

public class BrewingListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        BrewingInventory brewingInventory = new BrewingInventory(Main.getInstance().getConfig(), player);

        if (!event.getView().getTitle().contains(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "BREWING STAND"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String effectName = item.getItemMeta().getDisplayName();

        // inventory is the brewing stand menu
        if (event.getView().getTitle().contains(HexColor.HIGHLIGHT.toString() + ChatColor.BOLD + "BREWING STAND MENU")) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);

            if (effectName.contains("Permanent")) {
                player.openInventory(brewingInventory.permanentEffects);
            } else if (effectName.contains("Arrow")) {
                player.openInventory(brewingInventory.arrowEffects);
            }

            return;
        }

        // event.getInventory() ... top inventory = brewing stand
        // if the clicked inventory is not the brewing stand inventory, cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) return;

        // back to menu functionality
        if (effectName.equals(HexColor.RED + "Back To Menu")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            player.closeInventory();
            player.openInventory(brewingInventory.menu);
            return;
        }

        // effect already bought
        if (effectName.contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You have already bought this effect!");
            return;
        }

        // effect level not unlocked
        if (effectName.contains("ʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "Purchase the previous level to unlock this one!");
            return;
        }

        if (item.getType() == Material.TIPPED_ARROW) {
            if (effectName.contains("Unlock Arrow Effects")) {
                Effects.ArrowEffectsUnlocked.put(player, true);
                purchaseEffect(player, effectName);
                brewingInventory = new BrewingInventory(Main.getInstance().getConfig(), player);
                player.openInventory(brewingInventory.arrowEffects);
                return;
            }

            Effects.ArrowEffectType.put(player, item);
            player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
            brewingInventory = new BrewingInventory(Main.getInstance().getConfig(), player);
            player.openInventory(brewingInventory.arrowEffects);
            return;
        }

        // not enough coins to purchase the effect
        if (Coins.getCoins(player) < BrewingInventory.effectCost.get(effectName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You don't have enough coins! Need "
                    + HexColor.RED + (BrewingInventory.effectCost.get(effectName) - Coins.getCoins(player))
                    + ChatColor.RED + " more.");
            return;
        }

        // effect can be purchased - raise the level by 1
        if (effectName.contains("Speed")) {
            Effects.Speed.put(player, Effects.Speed.get(player) + 1);
            purchaseEffect(player, effectName);
        } else if (effectName.contains("Jump Boost")) {
            Effects.JumpBoost.put(player, Effects.JumpBoost.get(player) + 1);
            purchaseEffect(player, effectName);
        } else if (effectName.contains("Resistance")) {
            Effects.Resistance.put(player, Effects.Resistance.get(player) + 1);
            purchaseEffect(player, effectName);
        } else if (effectName.contains("Strength")) {
            Effects.Strength.put(player, Effects.Strength.get(player) + 1);
            purchaseEffect(player, effectName);
        } else if (effectName.contains("Regeneration")) {
            Effects.Regeneration.put(player, Effects.Regeneration.get(player) + 1);
            purchaseEffect(player, effectName);
        } else if (effectName.contains("Extra Hearts")) {
            Effects.ExtraHearts.put(player, Effects.ExtraHearts.get(player) + 1);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + 2.0);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            purchaseEffect(player, effectName);
        }

        // NOTE: always refresh the inventory instance before opening it again
        // so all data (Locked / Unlocked / Coins) updates after a purchase
        brewingInventory = new BrewingInventory(Main.getInstance().getConfig(), player);
        player.openInventory(brewingInventory.permanentEffects);
    }

    /**
     * @param player player that purchased an effect
     */
    private void purchaseEffect(Player player, String effectName) {
        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        Coins.removeCoins(player, BrewingInventory.effectCost.get(effectName), effectName);
        player.sendMessage(Prefixes.getCoinPrefix(-BrewingInventory.effectCost.get(effectName))
                + ChatColor.GRAY + "Purchased " + effectName + ChatColor.GRAY + ".");

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            // Substring 4 from effectName to remove formatting
            String query = "INSERT INTO Purchases (GameID, Player, purchasedObject, coinsBefore, cost, timestamp) " +
                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + effectName.substring(4)
                    + "', '" + (Coins.getCoins(player) + BrewingInventory.effectCost.get(effectName))
                    + "', '" + BrewingInventory.effectCost.get(effectName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
    }
}
