package com.tharox.dragonhunt.shop;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Item {
    private final Material material;
    private final int amount;
    protected int cost;
    private final String name;

    private final Map<Enchantment, Integer> enchantmentToLevelMap = new HashMap<>();

    private final Map<PotionEffectType, Integer> effectToAmplifier = new HashMap<>();
    private int duration = 1200;
    private Color color = Color.RED;

    public Item(ConfigurationSection section) {
        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        //default values
        cost = 0;

        this.material = material;
        this.amount = section.getInt("amount");
        this.cost = section.getInt("cost");
        this.name = section.getString("name");

        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");

        if (enchantmentsSection != null) {
            for (String enchantmentKey : enchantmentsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByKey(
                        NamespacedKey.minecraft(
                                enchantmentKey.toLowerCase()
                        )
                );

                if (enchantment != null) {
                    int level = enchantmentsSection.getInt(enchantmentKey);
                    enchantmentToLevelMap.put(enchantment, level);
                }
            }
        }

        ConfigurationSection effectsSection = section.getConfigurationSection("effects");

        if (effectsSection != null) {
            for (String effectKey : effectsSection.getKeys(false)) {
                PotionEffectType effect = PotionEffectType.getByName(effectKey.toLowerCase());

                if (effect != null) {
                    String[] effectValues = effectsSection.getString(effectKey).split(",");
                    int amplifier = Integer.parseInt(effectValues[0]);
                    effectToAmplifier.put(effect, amplifier);

                    this.duration = Integer.parseInt(effectValues[1]);
                }
            }
            this.color = Color.fromRGB(section.getInt("color"));
        }
    }

    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(material, amount);

        //potionMeta
        if (itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION ||
                itemStack.getType() == Material.LINGERING_POTION || itemStack.getType() == Material.TIPPED_ARROW) {

            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            if (potionMeta == null) return null;

            for (Map.Entry<PotionEffectType, Integer> effectEntry : effectToAmplifier.entrySet()) {
                potionMeta.addCustomEffect(new PotionEffect(
                                effectEntry.getKey(),
                                duration,
                                effectEntry.getValue()),
                        true
                );
            }

            potionMeta.setColor(color);

            itemStack.setItemMeta(addNameAndCost(potionMeta));

        //enchantmentStorageMeta
        } else if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            if (enchantmentStorageMeta == null) return null;

            for (Map.Entry<Enchantment, Integer> enchantEntry : enchantmentToLevelMap.entrySet()) {
                enchantmentStorageMeta.addStoredEnchant(
                        enchantEntry.getKey(),
                        enchantEntry.getValue(),
                        true
                );
            }

            itemStack.setItemMeta(addNameAndCost(enchantmentStorageMeta));

        //itemMeta
        } else {
            if(name != null && name.contains("Blocks & Redstone")) {
                itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) return null;

            for (Map.Entry<Enchantment, Integer> enchantEntry : enchantmentToLevelMap.entrySet()) {
                itemMeta.addEnchant(
                        enchantEntry.getKey(),
                        enchantEntry.getValue(),
                        true
                );
            }

            itemStack.setItemMeta(addNameAndCost(itemMeta));
        }

        return itemStack;
    }

    public ItemMeta addNameAndCost(ItemMeta meta) {
        if (name != null) {
            meta.setDisplayName(
                    ChatColor.translateAlternateColorCodes('&', name)
            );

            if(name.contains("»")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
        }

        if (cost != 0) {
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + cost + " Points",
                    ""));
        }
        return meta;
    }
}