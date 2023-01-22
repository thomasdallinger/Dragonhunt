package com.tharox.dragonhunt.enchanting;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class EnchantingItem {
    private final Material MATERIAL;
    private final int AMOUNT;
    private final int SLOT;
    public int cost;
    public final String NAME;
    private final String LORE;

    public EnchantingItem(ConfigurationSection section) {
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

    /**
     * Creates the actual ItemStack with the parameters (name, amount, slot, ...) provided.
     *
     * @return an ItemStack
     */
    public ItemStack createItem(Player player) {
        ItemStack itemToCreate = new ItemStack(MATERIAL, AMOUNT);

        if (NAME != null) {
            if (NAME.contains("Sharpness"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Sharpness.get(player));
            else if (NAME.contains("Fire Aspect"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.FireAspect.get(player));

            else if (NAME.contains("Power"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Power.get(player));
            else if (NAME.contains("Punch"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Punch.get(player));
            else if (NAME.contains("Flame"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Flame.get(player));

            else if (NAME.contains("Multishot"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Multishot.get(player));

            else if (NAME.contains("Protection"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Protection.get(player));
            else if (NAME.contains("Thorns"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Thorns.get(player));
            else if (NAME.contains("Feather Falling"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.FeatherFalling.get(player));
        }

        if (itemToCreate == null) return null;
        ItemMeta meta = itemToCreate.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        while (cost != 0) { // not looping while
            // player can't acquire the upgrade yet
            if (NAME != null && meta.getDisplayName().contains("UNLOCKED")) {
                meta.setLore(Arrays.asList(
                        HexColor.GREEN.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.WHITE.toString() + cost + " Coins",
                        HexColor.GREEN + "Unlocked!"
                ));
                break;

            }
            // player has already bought the upgrade
            else if (NAME != null && meta.getDisplayName().contains("LOCKED")) {
                meta.setLore(Arrays.asList(
                        HexColor.RED.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.WHITE.toString() + cost + " Coins",
                        ChatColor.DARK_RED + "Purchase previous levels first!"
                ));
                break;
            }

            // player has enough coins to buy the upgrade
            if (Coins.getCoins(player) >= cost) {
                meta.setLore(Arrays.asList(
                        ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                        "",
                        HexColor.HIGHLIGHT.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " Coins",
                        ChatColor.GOLD + "Click to buy!"
                ));
                break;
            }
            // player needs more coins to buy the upgrade
            else {
                meta.setLore(Arrays.asList(
                        ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.DARK_RED.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " Coins",
                        HexColor.RED.toString() + (cost - Coins.getCoins(player)) + " Coin(s) missing!"
                ));
                break;
            }
        }

        itemToCreate.setItemMeta(meta);

        return itemToCreate;
    }

    private ItemStack checkIfUnlocked(ItemStack itemStack, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        // item is purchasable
        if (AMOUNT == level) {
            return (setEnchantmentAndName(itemStack));
        }
        // item has already been purchased
        else if (AMOUNT < level) {
            itemMeta.setDisplayName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + NAME +
                    HexColor.GREEN + ChatColor.ITALIC + " UNLOCKED");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        // item has not been unlocked yet
        else {
            itemMeta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + NAME +
                    HexColor.RED + ChatColor.ITALIC + " LOCKED");
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
