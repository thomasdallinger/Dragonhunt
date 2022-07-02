package com.tharox.dragonhunt.upgrades;

import com.tharox.dragonhunt.points.UpgradePoints;
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
                itemStack = checkIfUnlocked(itemStack, WeaponUpgrade.Sharpness.get(player));
            else if (NAME.contains("Knockback"))
                itemStack = checkIfUnlocked(itemStack, WeaponUpgrade.Knockback.get(player));
            else if (NAME.contains("Fire Aspect"))
                itemStack = checkIfUnlocked(itemStack, WeaponUpgrade.FireAspect.get(player));
            else if (NAME.contains("Power"))
                itemStack = checkIfUnlocked(itemStack, BowUpgrade.Power.get(player));
            else if (NAME.contains("Punch"))
                itemStack = checkIfUnlocked(itemStack, BowUpgrade.Punch.get(player));
            else if (NAME.contains("Flame"))
                itemStack = checkIfUnlocked(itemStack, BowUpgrade.Flame.get(player));
            else if (NAME.contains("Piercing"))
                itemStack = checkIfUnlocked(itemStack, CrossbowUpgrade.Piercing.get(player));
            else if (NAME.contains("Multishot"))
                itemStack = checkIfUnlocked(itemStack, CrossbowUpgrade.Multishot.get(player));
            else if (NAME.contains("Quick Charge"))
                itemStack = checkIfUnlocked(itemStack, CrossbowUpgrade.QuickCharge.get(player));
            else if (NAME.contains("More Arrows"))
                itemStack = checkIfUnlocked(itemStack, ArrowUpgrade.MoreArrows.get(player));
            else if (NAME.contains("Effect Arrows"))
                itemStack = checkIfUnlocked(itemStack, ArrowUpgrade.EffectArrows.get(player));
            else if (NAME.contains("Efficiency"))
                itemStack = checkIfUnlocked(itemStack, ToolUpgrade.Efficiency.get(player));
            else if (NAME.contains("Unbreaking"))
                itemStack = checkIfUnlocked(itemStack, ToolUpgrade.Unbreaking.get(player));
            else if (NAME.contains("Protection"))
                itemStack = checkIfUnlocked(itemStack, ArmorUpgrade.Protection.get(player));
            else if (NAME.contains("Thorns"))
                itemStack = checkIfUnlocked(itemStack, ArmorUpgrade.Thorns.get(player));
            else if (NAME.contains("Feather Falling"))
                itemStack = checkIfUnlocked(itemStack, ArmorUpgrade.FeatherFalling.get(player));
            else if (NAME.contains("Depth Strider"))
                itemStack = checkIfUnlocked(itemStack, ArmorUpgrade.DepthStrider.get(player));
            else if (NAME.contains("Respiration"))
                itemStack = checkIfUnlocked(itemStack, ArmorUpgrade.Respiration.get(player));
            else if (NAME.contains("Aqua Affinity"))
                itemStack = checkIfUnlocked(itemStack, ArmorUpgrade.AquaAffinity.get(player));
            else if (NAME.contains("Food Source"))
                itemStack = checkIfUnlocked(itemStack, FoodUpgrade.FoodSource.get(player));
            else if (NAME.contains("Food Amount"))
                itemStack = checkIfUnlocked(itemStack, FoodUpgrade.FoodAmount.get(player));
            else if (NAME.contains("Smelted Resource"))
                itemStack = checkIfUnlocked(itemStack, ResourceUpgrade.SmeltedResource.get(player));
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

        if (cost != 0) {
            if (UpgradePoints.getPoints(player) >= cost) {
                if (NAME != null && itemMeta.getDisplayName().contains("LOCKED")) {
                    itemMeta.setLore(Arrays.asList(
                            ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                            "",
                            ChatColor.LIGHT_PURPLE.toString() + UpgradePoints.getPoints(player) +
                                    ChatColor.WHITE + " / " + cost + " Upgrade Points",
                            ChatColor.DARK_RED + "Purchase previous levels first!"
                    ));
                } else {
                    itemMeta.setLore(Arrays.asList(
                            ChatColor.YELLOW.toString() + ChatColor.ITALIC + LORE,
                            "",
                            ChatColor.LIGHT_PURPLE.toString() + UpgradePoints.getPoints(player) +
                                    ChatColor.WHITE + " / " + cost + " Upgrade Points",
                            ChatColor.GOLD + "Click to buy!"
                    ));
                }
            } else {
                itemMeta.setLore(Arrays.asList(
                        ChatColor.RED.toString() + ChatColor.ITALIC + LORE,
                        "",
                        ChatColor.DARK_RED.toString() + UpgradePoints.getPoints(player) +
                                ChatColor.WHITE + " / " + cost + " Upgrade Points",
                        ChatColor.RED.toString() + (cost - UpgradePoints.getPoints(player)) + " Upgrade Point(s) missing!"
                ));
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private ItemStack checkIfUnlocked(ItemStack itemStack, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        if (AMOUNT <= level) {
            return (setEnchantmentAndName(itemStack));
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