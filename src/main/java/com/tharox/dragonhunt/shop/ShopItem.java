package com.tharox.dragonhunt.shop;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.coins.Coins;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Objects;

public class ShopItem {
    private final int SLOT;
    public final String NAME;
    private final String LORE;
    private final Material MATERIAL;
    private final int AMOUNT;
    private final int COLOR;
    private PotionEffectType effectType;
    public int cost;
    public int value;

    public ShopItem(ConfigurationSection section) {
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

        if (LORE != null) {
            // Remove the gray color from the lore (7 = gray = number)
            this.value = getValueFromLore(LORE.substring(2));
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

        if (NAME != null) {
            // Menu item
            if (NAME.contains("»"))
                itemToCreate = setEnchantmentAndName(itemToCreate);

                // Enchantments
            else if (NAME.contains("Sharpness"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Sharpness.get(player));
            else if (NAME.contains("Knockback"))
                itemToCreate = checkIfUnlocked(itemToCreate, Enchantments.Knockback.get(player));
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

                // Permanent Effects
            else if (NAME.contains("Speed"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Speed.get(player));
            else if (NAME.contains("Jump Boost"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.JumpBoost.get(player));
            else if (NAME.contains("Haste"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Haste.get(player));
            else if (NAME.contains("Dolphins Grace"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.DolphinsGrace.get(player));
            else if (NAME.contains("Fire Resistance"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.FireResistance.get(player));
            else if (NAME.contains("Resistance"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Resistance.get(player));
            else if (NAME.contains("Strength"))
                itemToCreate = checkIfUnlocked(itemToCreate, Effects.Strength.get(player));

                // Negative Effects
            else if (NAME.contains("Unlock Arrow Effects")) {
                itemToCreate = unlockArrowEffects(itemToCreate, player);
            } else {
                itemToCreate = checkIfArrowEffectsUnlocked(itemToCreate, player);
            }
        }

        if (LORE != null) {
            // The itemToCreate might be null here due to the Ifs above - set it to 'default' again
            if (LORE.contains("Cooldown")) {
                itemToCreate = new ItemStack(MATERIAL, AMOUNT);
                itemToCreate = checkIfUnlocked(itemToCreate,
                        Upgrades.CooldownUpgradeLvl.getOrDefault(player, 1));
            } else if (LORE.contains("Duration")) {
                itemToCreate = new ItemStack(MATERIAL, AMOUNT);
                itemToCreate = checkIfUnlocked(itemToCreate,
                        Upgrades.DurationUpgradeLvl.getOrDefault(player, 1));
            } else if (LORE.contains("Effect")) {
                itemToCreate = new ItemStack(MATERIAL, AMOUNT);
                itemToCreate = checkIfUnlocked(itemToCreate,
                        Upgrades.EffectivenessUpgradeLvl.getOrDefault(player, 1));
            }
        }

        if (itemToCreate == null) return null;
        if (COLOR != 0) {
            PotionMeta potionMeta = (PotionMeta) itemToCreate.getItemMeta();
            if (potionMeta == null) return null;
            potionMeta.setColor(Color.fromRGB(COLOR));
            itemToCreate.setItemMeta(potionMeta);
        }

        ItemMeta meta = itemToCreate.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (NAME != null && NAME.contains("»") && LORE != null) {
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + LORE.substring(0, 34),
                    ChatColor.GRAY + LORE.substring(35)
            ));
        }

        while (cost != 0) { // not looping while
            // player has already bought the effect
            if (NAME != null && meta.getDisplayName().contains("ᴜɴʟᴏᴄᴋᴇᴅ")) {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE.toString() + cost + " ⛃",
                        HexColor.GREEN + "Unlocked!"
                ));
                break;

            }

            // player can't acquire the effect yet
            else if (NAME != null && meta.getDisplayName().contains("ʟᴏᴄᴋᴇᴅ")) {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE.toString() + cost + " ⛃",
                        HexColor.RED + "Purchase previous levels first!"
                ));
                break;
            }

            // player has enough coins to buy the effect
            if (Coins.getCoins(player) >= cost) {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE.toString() + cost + " ⛃",
                        HexColor.YELLOW + "Click to unlock!"
                ));
                break;
            }
            // player needs more coins to buy the effect
            else {
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + LORE,
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.WHITE.toString() + cost + " ⛃",
                        HexColor.RED.toString() + (cost - Coins.getCoins(player)) + " ⛃ missing!"
                ));
                break;
            }
        }

        itemToCreate.setItemMeta(meta);

        return itemToCreate;
    }

    private ItemStack checkIfUnlocked(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
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
        ItemMeta meta = item.getItemMeta();
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
        if (COLOR == 0) return null;
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        if (effectType != null) {
            meta.addCustomEffect(new PotionEffect(effectType, 0, 0), true);
        }

        ItemStack arrowStack = player.getInventory().getItem(17);
        PotionMeta arrowStackMeta = null;

        if (arrowStack != null && arrowStack.getType() == Material.TIPPED_ARROW) {
            arrowStackMeta = (PotionMeta) arrowStack.getItemMeta();
        }

        // item is selected
        if (arrowStackMeta != null && Objects.equals(arrowStackMeta.getCustomEffects().get(0).getType(),
                meta.getCustomEffects().get(0).getType())) {
            meta.setDisplayName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + NAME +
                    HexColor.GREEN + " ꜱᴇʟᴇᴄᴛᴇᴅ");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + LORE,
                    "",
                    HexColor.GREEN + "Currently selected!"
            ));
            item.setItemMeta(meta);

            return item;
        }

        // item is available
        if (Effects.ArrowEffectsUnlocked.get(player)) {
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + LORE,
                    "",
                    HexColor.YELLOW + "Click to get this effect!"
            ));
            item.setItemMeta(meta);

            return (setEnchantmentAndName(item));
        }

        // item is locked
        else {
            meta.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD + NAME +
                    HexColor.RED + " ʟᴏᴄᴋᴇᴅ");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + LORE,
                    "",
                    HexColor.RED + "Unlock Arrow Effects first!"
            ));
            item.setItemMeta(meta);
            return item;
        }
    }

    private ItemStack setEnchantmentAndName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.setDisplayName(HexColor.YELLOW.toString() + ChatColor.BOLD + NAME);

        item.setItemMeta(meta);

        return item;
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
