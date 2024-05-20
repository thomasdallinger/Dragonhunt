package com.tharox.dragonhunt.arrows;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.classes.FlyHigh;
import com.tharox.dragonhunt.shop.Effects;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Arrows {
    private static BukkitRunnable arrowRunnable;

    public static void initialize() {
        arrowRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                sharedLogic(true);
            }
        };

        // Start the runnable at the beginning of the game
        startRunnable();
    }

    public static void startRunnable() {
        arrowRunnable.runTaskTimer(Main.getInstance(), 0, 100);
    }

    // Static method to execute the logic immediately
    public static void executeLogicImmediately() {
        // Do not add an arrow but just change the effect of the arrow stack
        sharedLogic(false);
    }

    // Logic that should be executed both periodically and immediately
    private static void sharedLogic(boolean addArrow) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack arrowStack = player.getInventory().getItem(17);
            ItemStack generatedArrows;

            // List of all arrow effects
            List<HashMap<Player, Integer>> allArrowEffects = Arrays.asList(
                    Effects.ArrowMiningFatigue, Effects.ArrowPoison, Effects.ArrowBlindness,
                    Effects.ArrowWeakness, Effects.ArrowLevitation
            );

            // Find the arrow effect with the highest level
            HashMap<Player, Integer> highestArrowEffect = null;
            int highestLevel = 1;
            for (HashMap<Player, Integer> arrowEffect : allArrowEffects) {
                int level = arrowEffect.get(player);
                if (level > highestLevel) {
                    highestLevel = level;
                    highestArrowEffect = arrowEffect;
                }
            }

            // Check if the highest level is above 1
            if (highestLevel > 1) {
                generatedArrows = createEffectArrow(getArrowEffectType(highestArrowEffect),
                        getArrowColor(highestArrowEffect), highestLevel - 1);
            } else {
                generatedArrows = createArrow();
            }

            if (generatedArrows != null) {
                if (arrowStack != null && (arrowStack.getType() == Material.ARROW
                        || arrowStack.getType() == Material.TIPPED_ARROW)) {

                    // Keep the arrow amount the same (-1 +1 means +0)
                    if (!addArrow) {
                        arrowStack.setAmount(arrowStack.getAmount() - 1);
                    }

                    replaceCurrentArrowStack(arrowStack, generatedArrows, player);
                } else {
                    // replace current stack -> currently not arrows or tipped arrows
                    player.getInventory().setItem(17, generatedArrows);
                }
            }
        }
    }

    private static ItemStack createArrow() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Generated Arrow");
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack createEffectArrow(PotionEffectType effect, Color color, int level) {
        int duration = 40 * level; // In ticks.

        if (FlyHigh.active && effect.equals(PotionEffectType.LEVITATION)) {
            duration *= 2;
        }

        ItemStack item = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        // enchanted look
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.addCustomEffect(new PotionEffect(effect, duration, level - 1), true);
        meta.setColor(color);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Generated Arrow");
        meta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Duration: " + ChatColor.WHITE + (duration / 20) + " seconds",
                ChatColor.GRAY + "Level: " + ChatColor.WHITE + level
        ));
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Max amount of the arrow stack defined in this method.
     */
    private static void replaceCurrentArrowStack(ItemStack currentArrowStack, ItemStack generatedArrows, Player player) {
        int stackAmount = currentArrowStack.getAmount();
        int generatedAmount = generatedArrows.getAmount();
        generatedArrows.setAmount(Math.min((stackAmount + generatedAmount), 32));
        player.getInventory().setItem(17, generatedArrows);
    }

    private static PotionEffectType getArrowEffectType(HashMap<Player, Integer> arrowEffect) {
        if (arrowEffect == Effects.ArrowMiningFatigue) return PotionEffectType.SLOW_DIGGING;
        if (arrowEffect == Effects.ArrowPoison) return PotionEffectType.POISON;
        if (arrowEffect == Effects.ArrowBlindness) return PotionEffectType.BLINDNESS;
        if (arrowEffect == Effects.ArrowWeakness) return PotionEffectType.WEAKNESS;
        if (arrowEffect == Effects.ArrowLevitation) return PotionEffectType.LEVITATION;
        return null;
    }

    private static Color getArrowColor(HashMap<Player, Integer> arrowEffect) {
        if (arrowEffect == Effects.ArrowMiningFatigue) return Color.fromRGB(0x894E2E);
        if (arrowEffect == Effects.ArrowPoison) return Color.fromRGB(0x366622);
        if (arrowEffect == Effects.ArrowBlindness) return Color.fromRGB(0x1F1F23);
        if (arrowEffect == Effects.ArrowWeakness) return Color.fromRGB(0x484848);
        if (arrowEffect == Effects.ArrowLevitation) return Color.fromRGB(0xCEFFFF);
        return null;
    }
}