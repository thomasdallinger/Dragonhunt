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
    private PotionEffectType effectType;
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

        // effects are only used for arrows as a dummy effect
        try {
            effectType = PotionEffectType.getByName(section.getString("effect"));
        } catch (Exception e) {
            effectType = null;
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

        if (NAME != null) {
            // Menu item
            if (NAME.contains("»"))
                itemToCreate = setEnchantmentAndName(itemToCreate);

            // Permanent Effects
            else if (NAME.contains("Speed"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Speed.get(player));
            else if (NAME.contains("Jump Boost"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.JumpBoost.get(player));
            else if (NAME.contains("Resistance"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Resistance.get(player));
            else if (NAME.contains("Regeneration"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Regeneration.get(player));
            else if (NAME.contains("Extra Hearts")) {
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.ExtraHearts.get(player));
                this.cost = cost + (Effects.ExtraHearts.get(player) * 5);
            }

            // Negative Effects
            else if (NAME.contains("Unlock Arrow Effects")) {
                itemToCreate =unlockArrowEffects(itemToCreate, player);
            } else {
                itemToCreate = checkIfArrowEffectsUnlocked(itemToCreate, player);
            }
        }

        if (itemToCreate == null) return null;
        PotionMeta meta = (PotionMeta) itemToCreate.getItemMeta();
        if (meta == null) return null;

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
                        HexColor.GREEN.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " ⛃",
                        ChatColor.GOLD + "Click to unlock!"
                ));
                break;
            }
            // player needs more coins to buy the effect
            else {
                meta.setLore(Arrays.asList(
                        ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                        "",
                        HexColor.RED.toString() + Coins.getCoins(player) +
                                ChatColor.WHITE + " / " + cost + " ⛃",
                        ChatColor.DARK_RED.toString() + (cost - Coins.getCoins(player)) + " ⛃ missing!"
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
            // re-purchasable effects can be bought multiple times
            if (NAME.contains("ʀᴇ-ᴘᴜʀᴄʜᴀꜱᴇᴀʙʟᴇ")) {
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

    private ItemStack unlockArrowEffects(ItemStack item, Player player) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        // can be unlocked
        if (!Effects.ArrowEffectsUnlocked.get(player)) {
            return (setEnchantmentAndName(item));
        }

        // is already unlocked
        else {
            meta.setDisplayName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + NAME +
                    HexColor.GREEN + " ᴜɴʟᴏᴄᴋᴇᴅ");
            item.setItemMeta(meta);
            return item;
        }
    }

    /**
     * Opposite to unlockArrowEffects.
     * Lore is set here since the items have no cost.
     * Applies a dummy effect so the PotionEffectType can be used later.
     */
    private ItemStack checkIfArrowEffectsUnlocked(ItemStack item, Player player) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        if(effectType != null) {
            meta.addCustomEffect(new PotionEffect(effectType, 0, 0), true);
        }

        // item is available
        if (Effects.ArrowEffectsUnlocked.get(player)) {
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                    "",
                    ChatColor.GOLD + "Click to get this effect!"
            ));
            item.setItemMeta(meta);

            return (setEnchantmentAndName(item));
        }

        // item is locked
        else {
            meta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + NAME +
                    HexColor.RED + " ʟᴏᴄᴋᴇᴅ");
            meta.setLore(Arrays.asList(
                    HexColor.RED.toString() + ChatColor.ITALIC + LORE,
                    "",
                    ChatColor.DARK_RED + "Unlock Arrow Effects first!"
            ));
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
