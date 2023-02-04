package com.tharox.dragonhunt.players;

import org.bukkit.Material;
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
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        int amount = player.getInventory().getItemInMainHand().getAmount();

        if (item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            event.setCancelled(true);

            if(amount > 1) {
                player.getInventory().getItemInMainHand().setAmount(amount - 1);
            } else {
                player.getInventory().remove(item);
            }

            player.setFoodLevel(player.getFoodLevel() + 4);
            player.setSaturation((float) (player.getSaturation() + 9.6));

            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1));
            // = 4 yellow hearts
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
            // = heals half a heart every 25 ticks
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
            // = 20% damage reduction
        }
    }
}
