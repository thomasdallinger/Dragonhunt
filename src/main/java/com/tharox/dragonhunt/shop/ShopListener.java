package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.arrows.Arrows;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.game.GameState;
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
        ShopInventory brewingInventory = new ShopInventory(Main.getInstance().getConfig(), player);

        if (!event.getView().getTitle().contains("SHOP"))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;

        String itemName = item.getItemMeta().getDisplayName();
        String lore = "";
        int value = 0;

        if (item.getItemMeta().getLore() != null) {
            lore = item.getItemMeta().getLore().get(0).substring(2);
            value = getValueFromLore(lore);
        }

        // Inventory is the shop menu
        if (event.getView().getTitle().contains("SHOP MENU")) {
            player.closeInventory();

            if (itemName.contains("Enchantments")) {
                player.openInventory(brewingInventory.enchantments);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            } else if (itemName.contains("Permanent")) {
                player.openInventory(brewingInventory.permanentEffects);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            } else if (itemName.contains("Arrow")) {
                player.openInventory(brewingInventory.arrowEffects);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            } else if (itemName.contains("Kit")) {
                player.openInventory(brewingInventory.kitUpgrades);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            } else if (itemName.contains("Scout")) {
                player.performCommand("scout");
            } else if (itemName.contains("Set Respawn Point")) {
                player.performCommand("respawn");
            } else if (itemName.contains("Teleport to Top")) {
                player.performCommand("top");
            } else if (itemName.contains("Show Modifiers")) {
                player.performCommand("modifiers");
            } else if (itemName.contains("Teleport to Respawn Point")) {
                player.performCommand("respawn tp");
            }

            return;
        }

        // event.getInventory() ... top inventory = shop
        // if the clicked inventory is not the shop inventory, cancel the event
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) return;

        // Back to menu functionality
        if (itemName.equals(HexColor.RED + "Back to Menu")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            player.closeInventory();
            player.openInventory(brewingInventory.menu);
            return;
        }

        // Item already bought
        if (itemName.contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You have already purchased this effect!");
            return;
        }

        // Item level not unlocked
        if (itemName.contains("ʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "Purchase the previous level to unlock this one!");
            return;
        }

        // Unlock arrow effects
        if (item.getType() == Material.TIPPED_ARROW) {
            if (itemName.contains("Unlock Arrow Effects")) {
                if (!checkIfEnoughCoins(player, itemName)) return;

                Effects.ArrowEffectsUnlocked.put(player, true);
                purchaseItem(player, itemName);
                brewingInventory = new ShopInventory(Main.getInstance().getConfig(), player);
                player.openInventory(brewingInventory.arrowEffects);
                return;
            }

            Effects.ArrowEffectType.put(player, item);
            Arrows.executeLogicImmediately();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);

            brewingInventory = new ShopInventory(Main.getInstance().getConfig(), player);
            player.openInventory(brewingInventory.arrowEffects);
            return;
        }

        if (!checkIfEnoughCoins(player, itemName)) return;

        // effect can be purchased - raise the level by 1
        if (itemName.contains("Speed")) {
            Effects.Speed.put(player, Effects.Speed.get(player) + 1);
        } else if (itemName.contains("Jump Boost")) {
            Effects.JumpBoost.put(player, Effects.JumpBoost.get(player) + 1);
        } else if (itemName.contains("Haste")) {
            Effects.Haste.put(player, Effects.Haste.get(player) + 1);
        } else if (itemName.contains("Dolphins Grace")) {
            Effects.DolphinsGrace.put(player, Effects.DolphinsGrace.get(player) + 1);
        } else if (itemName.contains("Fire Resistance")) {
            Effects.FireResistance.put(player, Effects.FireResistance.get(player) + 1);
        } else if (itemName.contains("Resistance")) {
            Effects.Resistance.put(player, Effects.Resistance.get(player) + 1);
        } else if (itemName.contains("Strength")) {
            Effects.Strength.put(player, Effects.Strength.get(player) + 1);
        } else if (itemName.contains("Sharpness")) {
            Enchantments.Sharpness.put(player, Enchantments.Sharpness.get(player) + 1);
        } else if (itemName.contains("Knockback")) {
            Enchantments.Knockback.put(player, Enchantments.Knockback.get(player) + 1);
        } else if (itemName.contains("Fire Aspect")) {
            Enchantments.FireAspect.put(player, Enchantments.FireAspect.get(player) + 1);
        } else if (itemName.contains("Power")) {
            Enchantments.Power.put(player, Enchantments.Power.get(player) + 1);
        } else if (itemName.contains("Punch")) {
            Enchantments.Punch.put(player, Enchantments.Punch.get(player) + 1);
        } else if (itemName.contains("Flame")) {
            Enchantments.Flame.put(player, Enchantments.Flame.get(player) + 1);
        } else if (itemName.contains("Multishot")) {
            Enchantments.Multishot.put(player, Enchantments.Multishot.get(player) + 1);
        } else if (itemName.contains("Protection")) {
            Enchantments.Protection.put(player, Enchantments.Protection.get(player) + 1);
        } else if (itemName.contains("Thorns")) {
            Enchantments.Thorns.put(player, Enchantments.Thorns.get(player) + 1);
        } else if (itemName.contains("Feather Falling")) {
            Enchantments.FeatherFalling.put(player, Enchantments.FeatherFalling.get(player) + 1);
        }

        if (lore.contains("Cooldown")) {
            Upgrades.Cooldown.put(player, value);
            Upgrades.CooldownUpgradeLvl.put(
                    player, Upgrades.CooldownUpgradeLvl.getOrDefault(player, 1) + 1);
        } else if (lore.contains("Duration")) {
            Upgrades.Duration.put(player, value);
            Upgrades.DurationUpgradeLvl.put(
                    player, Upgrades.DurationUpgradeLvl.getOrDefault(player, 1) + 1);
        } else if (lore.contains("Effect")) {
            Upgrades.Effectiveness.put(player, value);
            Upgrades.EffectivenessUpgradeLvl.put(
                    player, Upgrades.EffectivenessUpgradeLvl.getOrDefault(player, 1) + 1);
        }

        purchaseItem(player, itemName);

        // NOTE: always refresh the inventory instance before opening it again
        // so all data (Locked / Unlocked / Coins) updates after a purchase
        brewingInventory = new ShopInventory(Main.getInstance().getConfig(), player);
        if (event.getView().getTitle().contains("ENCHANTMENTS"))
            player.openInventory(brewingInventory.enchantments);
        else if (event.getView().getTitle().contains("EFFECTS"))
            player.openInventory(brewingInventory.permanentEffects);
        else if (event.getView().getTitle().contains("ARROWS"))
            player.openInventory(brewingInventory.arrowEffects);
        else if (event.getView().getTitle().contains("UPGRADES"))
            player.openInventory(brewingInventory.kitUpgrades);
    }

    private boolean checkIfEnoughCoins(Player player, String itemName) {
        if (Coins.getCoins(player) < ShopInventory.itemCost.get(itemName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + "You do not have enough ⛃! Need "
                    + HexColor.RED + (ShopInventory.itemCost.get(itemName) - Coins.getCoins(player))
                    + ChatColor.RED + " more.");

            return false;
        }

        return true;
    }

    /**
     * @param player player that purchased an item
     */
    private void purchaseItem(Player player, String itemName) {
        player.sendMessage(Prefixes.getCoinPrefix(-ShopInventory.itemCost.get(itemName))
                + ChatColor.GRAY + "Purchased " + HexColor.YELLOW + itemName.substring(16) + ChatColor.GRAY + ".");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
        Coins.removeCoins(player, ShopInventory.itemCost.get(itemName));

        DatabaseExecutor.executeDatabaseHandler((con) -> {
            // Substring 16 from effectName to remove Hex-formatting
            String query = "INSERT INTO purchases (game_id, player, purchased_item, coins_before, cost, timestamp) " +
                    "VALUES ('" + GameState.gameID + "', '" + player.getUniqueId()
                    + "', '" + itemName.substring(16)
                    + "', '" + (Coins.getCoins(player) + ShopInventory.itemCost.get(itemName))
                    + "', '" + ShopInventory.itemCost.get(itemName)
                    + "', '" + DbValues.getTimestamp() + "')";

            Statement stmt = con.createStatement();
            stmt.execute(query);
            stmt.close();
        });
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
