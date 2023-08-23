package com.tharox.dragonhunt.kits.upgrades;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.enchanting.Enchantments;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class UpgradesItem {
    private final int SLOT;
    public final String NAME;
    private final String LORE;
    private final Material MATERIAL;
    private final int AMOUNT;
    public int cost;
    public int value;

    public UpgradesItem(ConfigurationSection section) {
        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        // Default values
        cost = 0;

        this.SLOT = section.getInt("slot");
        this.NAME = section.getString("name");
        this.LORE = section.getString("lore");
        this.MATERIAL = material;
        this.AMOUNT = section.getInt("amount");
        this.cost = section.getInt("cost");

        if (LORE != null) {
            this.value = getValueFromLore(LORE);
        } else {
            this.value = 0;
        }
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

        if (LORE != null) {
            if (LORE.contains("Cooldown"))
                itemToCreate = checkIfUnlocked(itemToCreate, Upgrades.Cooldown.getOrDefault(player, 0));
            else if (LORE.contains("Duration"))
                itemToCreate = checkIfUnlocked(itemToCreate, Upgrades.Duration.getOrDefault(player, 0));
            else if (LORE.contains("Effectiveness"))
                itemToCreate = checkIfUnlocked(itemToCreate, Upgrades.Effectiveness.getOrDefault(player, 0));
        }

        if (itemToCreate == null) return null;
        ItemMeta meta = itemToCreate.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        while (cost != 0) { // not looping while
            // player has already bought the upgrade
            if (NAME != null && meta.getDisplayName().contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE + cost + " ⛃",
                        HexColor.GREEN + "Unlocked!"
                ));
                break;

            }

            // player can't acquire the upgrade yet
            else if (NAME != null && meta.getDisplayName().contains("ʟᴏᴄᴋᴇᴅ")) {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE + cost + " ⛃",
                        HexColor.RED + "Purchase previous levels first!"
                ));
                break;
            }

            // player has enough coins to buy the upgrade
            if (Coins.getCoins(player) >= cost) {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE + cost + " ⛃",
                        HexColor.YELLOW + "Click to unlock!"
                ));
                break;
            }
            // player needs more coins to buy the upgrade
            else {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE + cost + " ⛃",
                        HexColor.RED.toString() + (cost - Coins.getCoins(player)) + " ⛃ missing!"
                ));
                break;
            }
        }

        itemToCreate.setItemMeta(meta);

        return itemToCreate;
    }

    private ItemStack checkIfUnlocked(ItemStack itemStack, int upgradeValue) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        // item has already been purchased
        if (upgradeValue >= value) {
            meta.setDisplayName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + NAME +
                    HexColor.GREEN + " ᴜɴʟᴏᴄᴋᴇᴅ");
            itemStack.setItemMeta(meta);
            return itemStack;
        }
        // item lvl 1 is purchasable
        else if (upgradeValue >= 0 && AMOUNT == 1) {
            return (setEnchantmentAndName(itemStack));
        }
        // item lvl 2 is purchasable
        else if (upgradeValue >= 0 && AMOUNT == 2) {
            return (setEnchantmentAndName(itemStack));
        }
        // item lvl 3 is purchasable
        else if (upgradeValue >= 0 && AMOUNT == 3) {
            return (setEnchantmentAndName(itemStack));
        }
        // item has not been unlocked yet
        else {
            meta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + NAME +
                    HexColor.RED + " ʟᴏᴄᴋᴇᴅ");
            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }

    private ItemStack setEnchantmentAndName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.setDisplayName(HexColor.YELLOW.toString() + ChatColor.BOLD + NAME);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
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
