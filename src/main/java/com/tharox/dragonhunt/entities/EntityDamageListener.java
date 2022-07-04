package com.tharox.dragonhunt.entities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Arrow arrow;
        ProjectileSource shooter;
        Player player;
        ItemStack itemInMainHand; // Grappler can never be in a players Offhand

        if (damager instanceof Arrow) {
            arrow = (Arrow) damager;
            shooter = arrow.getShooter();
        } else return;

        if (shooter instanceof Player) {
            player = (Player) shooter;
        } else return;

        itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() == null) return;
        if (itemInMainHand.getItemMeta().getDisplayName().equals(
                ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Grappler")) {
            arrow.remove();
            event.setCancelled(true);
        }
    }
}
