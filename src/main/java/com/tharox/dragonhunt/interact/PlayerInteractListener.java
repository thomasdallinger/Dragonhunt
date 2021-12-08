package com.tharox.dragonhunt.interact;

import com.tharox.dragonhunt.points.Points;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        if(player.getWorld().getName().equals("world_nether")) { //Not able to throw enderpearls in the nether
            if(player.getInventory().getItemInMainHand().getType().equals(Material.ENDER_PEARL) || player.getInventory().getItemInOffHand().getType().equals(Material.ENDER_PEARL)) {
                event.setCancelled(true);
            }
            if(player.getInventory().getItemInMainHand().getType().equals(Material.SPLASH_POTION)) {
                event.setCancelled(true);
            }
        }

        if (e.equals(EquipmentSlot.HAND) && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            if (block != null) {
                if (block.getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                    block.setType(Material.AIR);

                    int points = 200;
                    if (player.getWorld().getName().equals("world"))
                        Points.addPoints(player, points);

                    return;
                }

                if (block.getType() == Material.CHEST) {
                    event.setCancelled(true);
                    block.setType(Material.ENDER_CHEST);
                    return;
                }

                if (block.getType() == Material.CHEST_MINECART) {
                    event.setCancelled(true);
                    block.setType(Material.ENDER_CHEST);
                    return;
                }

                if (block.getType() == Material.ENCHANTING_TABLE || block.getType() == Material.BREWING_STAND) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    player.sendMessage(ChatColor.RED + "You are not allowed to use this block!");
                }
            }
        }
    }
}
