package com.tharox.dragonhunt.brewing;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BrewingItem {
    private final int SLOT;
    private final String NAME;
    private final Material MATERIAL;
    private final int AMOUNT;
    protected final int COST;

    private final Map<PotionEffectType, Integer> effectToAmplifier = new HashMap<>();
    private int duration;
    private Color color;

    public BrewingItem(ConfigurationSection section) {
        Material material;

        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        this.SLOT = section.getInt("slot");
        this.NAME = section.getString("name");
        this.MATERIAL = material;
        this.AMOUNT = section.getInt("amount");
        this.COST = section.getInt("cost");

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
        ItemStack item = new ItemStack(MATERIAL, AMOUNT);

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        // add effect: type, duration and amplifier
        for (Map.Entry<PotionEffectType, Integer> effectEntry : effectToAmplifier.entrySet()) {
            meta.addCustomEffect(new PotionEffect(
                            effectEntry.getKey(),
                            duration,
                            effectEntry.getValue()),
                    true
            );
        }

        // set color
        meta.setColor(color);

        // add name, costs and whether the player is able to purchase the item or not
        if (Coins.getCoins(player) >= COST) {
            meta.setDisplayName(ChatColor.AQUA + NAME);
            meta.setLore(Arrays.asList(
                    "",
                    HexColor.HIGHLIGHT.toString() + Coins.getCoins(player) +
                            ChatColor.WHITE + " / " + COST + " ⛃",
                    ChatColor.GOLD + "Click to buy!"
            ));
        } else {
            meta.setDisplayName(HexColor.RED + NAME);
            meta.setLore(Arrays.asList(
                    "",
                    ChatColor.DARK_RED.toString() + Coins.getCoins(player) +
                            ChatColor.WHITE + " / " + COST + " ⛃",
                    HexColor.RED.toString() + (COST - Coins.getCoins(player)) + " Coin(s) missing!"
            ));
        }

        item.setItemMeta(meta);

        return item;
    }

}
