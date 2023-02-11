package com.tharox.dragonhunt.arrows;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileHitListener implements Listener {

    /**
     * Arrows disappear when hitting an entity / the ground.
     */
    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        AbstractArrow arrow;
        ProjectileSource shooter = event.getEntity().getShooter();
        Player player;
        ItemStack itemInMainHand; // Grappler can never be in a players Offhand

        if (event.getEntity() instanceof AbstractArrow) {
            arrow = (AbstractArrow) event.getEntity();
        } else return;

        if (shooter instanceof Player) {
            player = (Player) shooter;
        } else return;

        // If the Grappler shot the arrow do not remove the arrow.
        itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() == null) return;
        if (!itemInMainHand.getItemMeta().getDisplayName().contains("Grappler")) {
            arrow.remove();
        }
    }
}
