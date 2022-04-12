package com.tharox.dragonhunt.points;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class LevelUp implements Listener {

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.sendTitle(ChatColor.AQUA + " ", ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "LEVEL "
                + event.getNewLevel(), 5, 60, 5);
        player.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "XP Level Rewards:");
        GearPoints.addPoints(player, 1);

        if(event.getNewLevel() % 5 == 0) {
            player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "+1"
                    + ChatColor.GOLD + " Golden Apple");
            HashMap<Integer, ItemStack> didNotFit = player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
            if(!didNotFit.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), didNotFit.get(0)); //index 0 = max. only 1 item in the hashmap
            }
        }
    }
}
