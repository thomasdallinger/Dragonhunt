package com.tharox.dragonhunt.arrows;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.modifiers.classes.EnhancedArrowEffects;
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

            // if arrow effects have been unlocked AND one of the available arrow effects has been selected
            if (Effects.ArrowEffectsUnlocked.get(player)
                    && Effects.ArrowEffectType.get(player).getType() == Material.TIPPED_ARROW) {
                ItemStack arrowToUse = Effects.ArrowEffectType.get(player);
                PotionMeta meta = (PotionMeta) arrowToUse.getItemMeta();
                // index is always 0 since there is only one effect on the arrow at a time
                generatedArrows = createEffectArrow(meta.getCustomEffects().get(0).getType(),
                        meta.getColor());
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

    private static ItemStack createEffectArrow(PotionEffectType effect, Color color) {
        int duration = 120; // In ticks.
        int amplifier = 1;

        if (EnhancedArrowEffects.active) {
            duration = 180;
        }

        ItemStack item = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        // enchanted look
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.addCustomEffect(new PotionEffect(effect, duration, amplifier), true);
        meta.setColor(color);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Generated Arrow");
        meta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Duration: " + ChatColor.WHITE + (duration / 20) + " seconds",
                ChatColor.GRAY + "Level: " + ChatColor.WHITE + (amplifier + 1)
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
}