package com.tharox.dragonhunt.arrows;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.shop.ArrowUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class Arrows {
    /**
     * Arrow generation is started once the elytra countdown after the game start hits 0.
     */
    public Arrows() {
        generateArrows();
    }

    public void generateArrows() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack arrowStack = player.getInventory().getItem(17);
                    ItemStack generatedArrows;
                    PotionEffectType arrowEffect;
                    Color color;
                    int amount = ArrowUpgrade.MoreArrows.get(player);

                    if (arrowStack == null
                            || (arrowStack.getType() != Material.ARROW && arrowStack.getType() != Material.TIPPED_ARROW)
                            || arrowStack.getAmount() <= 64) {
                        if (ArrowUpgrade.EffectArrows.get(player) > 1) {

                            switch (ArrowUpgrade.EffectArrows.get(player)) {
                                case 2:
                                    arrowEffect = PotionEffectType.SLOW;
                                    color = Color.GRAY;
                                    break;
                                case 3:
                                    arrowEffect = PotionEffectType.BLINDNESS;
                                    color = Color.BLACK;
                                    break;
                                case 4:
                                    arrowEffect = PotionEffectType.POISON;
                                    color = Color.GREEN;
                                    break;
                                default:
                                    arrowEffect = null;
                                    color = null;
                            }
                            generatedArrows = createEffectArrow(amount, arrowEffect, color);
                        } else {
                            generatedArrows = createArrow(amount);
                        }
                        if (generatedArrows != null)
                            if(arrowStack != null && (arrowStack.getType() == Material.ARROW || arrowStack.getType() == Material.TIPPED_ARROW))
                                replaceCurrentArrowStack(arrowStack, generatedArrows, player);
                            else
                                //replaceCurrentStack -> currently not arrows or tipped arrows
                                player.getInventory().setItem(17, generatedArrows);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 200);
    }

    private ItemStack createArrow(int amount) {
        ItemStack item = new ItemStack(Material.ARROW, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.GOLD + "Generated Arrow");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createEffectArrow(int amount, PotionEffectType effect, Color color) {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW, amount);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return null;

        meta.addCustomEffect(new PotionEffect(effect, 100, 0), true);
        meta.setColor(color);
        meta.setDisplayName(ChatColor.GOLD + "Generated Arrow");
        meta.setLore(Arrays.asList(
                ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Effect duration: 5 seconds",
                ""));
        item.setItemMeta(meta);

        return item;
    }

    private void replaceCurrentArrowStack(ItemStack currentArrowStack, ItemStack generatedArrows, Player player) {
        int stackAmount = currentArrowStack.getAmount();
        int generatedAmount = generatedArrows.getAmount();
        generatedArrows.setAmount(Math.min((stackAmount + generatedAmount), 64));
        player.getInventory().setItem(17, generatedArrows);
    }
}