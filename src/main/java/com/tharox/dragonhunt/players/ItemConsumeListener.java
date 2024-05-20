package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.modifiers.classes.ChristmasCookies;
import com.tharox.dragonhunt.modifiers.classes.FireResistance;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemConsumeListener implements Listener {

    @EventHandler
    public void onItemConsumption(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        int amount = player.getInventory().getItemInMainHand().getAmount();

        if (item.getType() == Material.COOKIE) {
            if (ChristmasCookies.active) {
                player.setFoodLevel(player.getFoodLevel() + 2);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 7 * 20, 1));
            }
        }

        if (item.getType().isEdible()) {
            if (FireResistance.active) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 20, 0));
            }
        }

        if (item.getType() == Material.APPLE) {
            player.setFoodLevel(player.getFoodLevel() + 2);
        }

        if (item.getType() == Material.GOLDEN_APPLE) {
            event.setCancelled(true);

            if (amount > 1) {
                player.getInventory().getItemInMainHand().setAmount(amount - 1);
            } else {
                player.getInventory().remove(item);
            }

            player.setFoodLevel(player.getFoodLevel() + 6);
            player.setSaturation((float) (player.getSaturation() + 9.6));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

            // Absorption 1 (1:30)
            // Regeneration 2 (0:05)

            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1800, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        }

        if (item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            event.setCancelled(true);

            if (amount > 1) {
                player.getInventory().getItemInMainHand().setAmount(amount - 1);
            } else {
                player.getInventory().remove(item);
            }

            player.setFoodLevel(player.getFoodLevel() + 6);
            player.setSaturation((float) (player.getSaturation() + 9.6));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

            // Absorption 3 (1:30)
            // Regeneration 2 (0:15)

            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1800, 2));
            // = 4 yellow hearts
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 1));
            // = heals 2 hearts every 5 seconds
        }
    }
}
