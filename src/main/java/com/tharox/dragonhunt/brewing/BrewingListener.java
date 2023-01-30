package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
            menuNavigation(player, effectName);
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
        if (effectName.equals(HexColor.RED + "Back To Menu")) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            player.closeInventory();
            player.openInventory(brewingInventory.menu);
            return;
        }

        // upgrade already bought
        if (effectName.contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You have already bought this effect!");
            return;
        }

        // upgrade level not unlocked
        if (effectName.contains("ʟᴏᴄᴋᴇᴅ")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY
                    + "Purchase the previous level to unlock this one!");
            return;
        }

        // not enough points to purchase the effect
        if (Coins.getCoins(player) < BrewingInventory.effectCost.get(effectName)) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            player.sendMessage(Prefixes.getErrorPrefix() + ChatColor.GRAY + "You don't have enough coins! Need "
                    + HexColor.RED + (BrewingInventory.effectCost.get(effectName) - Coins.getCoins(player))
                    + ChatColor.GRAY + " more.");
            return;
        }

        // effect can be purchased - raise the level by 1
        if (effectName.contains("Speed")) {
            Effects.Speed.put(player, Effects.Speed.get(player) + 1);
            purchaseEffect(player, effectName, "Permanent");
        } else if (effectName.contains("Haste")) {
            Effects.Haste.put(player, Effects.Haste.get(player) + 1);
            purchaseEffect(player, effectName, "Permanent");
        } else if (effectName.contains("Resistance")) {
            Effects.Resistance.put(player, Effects.Resistance.get(player) + 1);
            purchaseEffect(player, effectName, "Permanent");
        } else if (effectName.contains("Regeneration")) {
            Effects.Regeneration.put(player, Effects.Regeneration.get(player) + 1);
            purchaseEffect(player, effectName, "Permanent");
        } else if (effectName.contains("Extra Hearts")) {
            Effects.ExtraHearts.put(player, Effects.ExtraHearts.get(player) + 1);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + 2.0);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            purchaseEffect(player, effectName, "Permanent");
        }
    }

    /**
     * @param player    player that purchased an effect
     * @param inventory inventory to open (refresh after a short delay)
     */
    private void purchaseEffect(Player player, String upgradeName, String inventory) {
        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        Coins.removeCoins(player, BrewingInventory.effectCost.get(upgradeName), upgradeName);

        menuNavigation(player, inventory);
    }

    private void menuNavigation(Player player, String name) {
        BrewingInventory brewingInventory = new BrewingInventory(Main.getInstance().getConfig(), player);

        if (name.contains("Permanent"))
            player.openInventory(brewingInventory.permanentEffects);
        else if (name.contains("Arrow"))
            player.openInventory(brewingInventory.arrowEffects);
        else if (name.contains("Sword"))
            player.openInventory(brewingInventory.swordEffects);
    }
}
