package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class BrewingItem {
    private final int SLOT;
    public final String NAME;
    private final String LORE;
    private final Material MATERIAL;
    private final int AMOUNT;
    private final int COLOR;
    public int cost;

    public BrewingItem(ConfigurationSection section) {
        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        //default values
        cost = 0;

        this.SLOT = section.getInt("slot");
        this.NAME = section.getString("name");
        this.LORE = section.getString("lore");
        this.MATERIAL = material;
        this.AMOUNT = section.getInt("amount");
        this.COLOR = section.getInt("color");
        this.cost = section.getInt("cost");
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
            if (NAME.contains("»"))
                itemToCreate = setEnchantmentAndName(itemToCreate); // menu item
            else if (NAME.contains("Speed"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Speed.get(player));
            else if (NAME.contains("Haste"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Haste.get(player));
            else if (NAME.contains("Resistance"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Resistance.get(player));
            else if (NAME.contains("Regeneration"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Regeneration.get(player));
            else if (NAME.contains("Extra Hearts")) {
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.ExtraHearts.get(player));
                this.cost = cost + (Effects.ExtraHearts.get(player) * 10);
            }

        }

        if (itemToCreate == null) return null;
        PotionMeta meta = (PotionMeta) itemToCreate.getItemMeta();
        if (meta == null) return null;

        meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0), true); // dummy effect
        meta.setColor(Color.fromRGB(COLOR));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (NAME != null && NAME.contains("»") && LORE != null) {
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE.substring(0, 34),
                    ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE.substring(35)
            ));
        }

        while (cost != 0) { // not looping while
            // player can't acquire the effect yet
            if (NAME != null && meta.getDisplayName().contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
                meta.setLore(Arrays.asList(
                        HexColor.GREEN.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.WHITE.toString() + cost + " ⛃",
                        HexColor.GREEN + "Unlocked!"
                ));
                break;

            }
            // player has already bought the effect
            else if (NAME != null && meta.getDisplayName().contains("ʟᴏᴄᴋᴇᴅ")) {
                meta.setLore(Arrays.asList(
                        HexColor.RED.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.WHITE.toString() + cost + " ⛃",
                        ChatColor.DARK_RED + "Purchase previous levels first!"
                ));
                break;
            }

            // player has enough coins to buy the effect
            if (Coins.getCoins(player) >= cost) {
                meta.setLore(Arrays.asList(
                        ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                        "",
                        HexColor.HIGHLIGHT.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " ⛃",
                        ChatColor.GOLD + "Click to buy!"
                ));
                break;
            }
            // player needs more coins to buy the effect
            else {
                meta.setLore(Arrays.asList(
                        ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.DARK_RED.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " ⛃",
                        HexColor.RED.toString() + (cost - Coins.getCoins(player)) + " Coin(s) missing!"
                ));
                break;
            }
        }

        itemToCreate.setItemMeta(meta);

        return itemToCreate;
    }

    private ItemStack checkIfUnlocked(ItemStack item, int level) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        // item is purchasable
        if (AMOUNT == level) {
            return (setEnchantmentAndName(item));
        }
        // item has already been purchased
        else if (AMOUNT < level) {
            // Extra Hearts should be re-purchasable
            if (NAME.contains("Extra Hearts")) {
                return (setEnchantmentAndName(item));
            } else {
                meta.setDisplayName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + NAME +
                        HexColor.GREEN + " ᴜɴʟᴏᴄᴋᴇᴅ");
                item.setItemMeta(meta);
                return item;
            }
        }
        // item has not been unlocked yet
        else {
            meta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + NAME +
                    HexColor.RED + " ʟᴏᴄᴋᴇᴅ");
            item.setItemMeta(meta);
            return item;
        }
    }

    private ItemStack setEnchantmentAndName(ItemStack item) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + NAME);

        item.setItemMeta(meta);

        return item;
    }
}
