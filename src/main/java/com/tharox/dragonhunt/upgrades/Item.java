package com.tharox.dragonhunt.upgrades;

import com.tharox.dragonhunt.coins.Coins;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Item {
    private final Material MATERIAL;
    private final int AMOUNT;
    private final int SLOT;
    public int cost;
    private final String NAME;
    private final String LORE;

    public Item(ConfigurationSection section) {
        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        //default values
        cost = 0;

        this.MATERIAL = material;
        this.AMOUNT = section.getInt("amount");
        this.SLOT = section.getInt("slot");
        this.cost = section.getInt("cost");
        this.NAME = section.getString("name");
        this.LORE = section.getString("lore");
    }

    /**
     * @return index of where the item is supposed to be placed in the inventory
     */
    public int getSlot() {
        return SLOT;
    }

    public ItemStack createItem(Player player) {
        ItemStack itemStack = new ItemStack(MATERIAL, AMOUNT);

        if (NAME != null) {
            if (NAME.contains("»"))
                itemStack = setEnchantmentAndName(itemStack);
            else if (NAME.contains("Sharpness"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Sharpness.get(player));
            else if (NAME.contains("Knockback"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Knockback.get(player));
            else if (NAME.contains("Fire Aspect"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.FireAspect.get(player));
            else if (NAME.contains("Power"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Power.get(player));
            else if (NAME.contains("Punch"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Punch.get(player));
            else if (NAME.contains("Flame"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Flame.get(player));
            else if (NAME.contains("Piercing"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Piercing.get(player));
            else if (NAME.contains("Multishot"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Multishot.get(player));
            else if (NAME.contains("Quick Charge"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.QuickCharge.get(player));
            else if (NAME.contains("More Arrows"))
                itemStack = checkIfUnlocked(itemStack, ArrowUpgrade.MoreArrows.get(player));
            else if (NAME.contains("Effect Arrows"))
                itemStack = checkIfUnlocked(itemStack, ArrowUpgrade.EffectArrows.get(player));
            else if (NAME.contains("Efficiency"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Efficiency.get(player));
            else if (NAME.contains("Unbreakable"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Unbreakable.get(player));
            else if (NAME.contains("Protection"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Protection.get(player));
            else if (NAME.contains("Thorns"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.Thorns.get(player));
            else if (NAME.contains("Feather Falling"))
                itemStack = checkIfUnlocked(itemStack, EnchantmentUpgrades.FeatherFalling.get(player));
            else if (NAME.contains("Food Source"))
                itemStack = checkIfUnlocked(itemStack, FoodUpgrade.FoodSource.get(player));
            else if (NAME.contains("Food Amount"))
                itemStack = checkIfUnlocked(itemStack, FoodUpgrade.FoodAmount.get(player));
            else if (NAME.contains("Resource Amount"))
                itemStack = checkIfUnlocked(itemStack, ResourceUpgrade.ResourceAmount.get(player));
            else if (NAME.contains("More XP"))
                itemStack = checkIfUnlocked(itemStack, XPUpgrade.MoreXP.get(player));
        }

        if (itemStack == null) return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (NAME != null && NAME.contains("»") && LORE != null) {
            itemMeta.setLore(Arrays.asList(
                    ChatColor.YELLOW + ChatColor.ITALIC.toString() + LORE.substring(0, 34),
                    ChatColor.YELLOW + ChatColor.ITALIC.toString() + LORE.substring(35)
            ));
        }

        while (cost != 0) { // not looping while
            // player can't acquire the upgrade yet
            if (NAME != null && itemMeta.getDisplayName().contains("UNLOCKED")) {
                itemMeta.setLore(Arrays.asList(
                        ChatColor.GREEN.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.WHITE.toString() + cost + " Coins",
                        ChatColor.GREEN + "Unlocked!"
                ));
                break; // player has already bought the upgrade
            } else if (NAME != null && itemMeta.getDisplayName().contains("LOCKED")) {
                itemMeta.setLore(Arrays.asList(
                        ChatColor.RED.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.WHITE.toString() + cost + " Coins",
                        ChatColor.DARK_RED + "Purchase previous levels first!"
                ));
                break;
            }

            // player has enough coins to buy the upgrade
            if (Coins.getCoins(player) >= cost) {
                itemMeta.setLore(Arrays.asList(
                        ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.LIGHT_PURPLE.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " Coins",
                        ChatColor.GOLD + "Click to buy!"
                ));
                break;
            } else { // player needs more coins to buy the upgrade
                itemMeta.setLore(Arrays.asList(
                        ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.DARK_RED.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " Coins",
                        ChatColor.RED.toString() + (cost - Coins.getCoins(player)) + " Coin(s) missing!"
                ));
                break;
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private ItemStack checkIfUnlocked(ItemStack itemStack, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        // item is purchasable
        if (AMOUNT == level) {
            return (setEnchantmentAndName(itemStack));

            // item has already been purchased
        } else if (AMOUNT < level) {
            itemMeta.setDisplayName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + NAME +
                    ChatColor.GREEN + ChatColor.ITALIC + " UNLOCKED");
            itemStack.setItemMeta(itemMeta);
            return itemStack;

            // item can't be purchased yet
        } else {
            itemMeta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + NAME +
                    ChatColor.RED + ChatColor.ITALIC + " LOCKED");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
    }

    private ItemStack setEnchantmentAndName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + NAME);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}