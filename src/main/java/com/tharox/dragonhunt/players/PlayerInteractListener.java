package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.upgrades.XPUpgrade;
import com.tharox.dragonhunt.points.UpgradePoints;
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
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        if (e.equals(EquipmentSlot.HAND) &&
                (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            if (block != null) {
                if (block.getType() == Material.ENDER_CHEST) {
                    event.setCancelled(true);
                    block.setType(Material.AIR);

                    player.giveExp(UpgradePoints.bonusXP + XPUpgrade.MoreXP.get(player) - 1);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    return;
                }

                if (block.getType() == Material.CHEST || block.getType() == Material.CHEST_MINECART) {
                    event.setCancelled(true);
                    block.setType(Material.ENDER_CHEST);
                    return;
                }

                if (block.getType() == Material.ENCHANTING_TABLE
                        || block.getType() == Material.BREWING_STAND
                        || block.getType() == Material.ANVIL) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    player.sendMessage(ChatColor.RED + "You are not allowed to use this block!");
                }
            }
        }
    }
}
