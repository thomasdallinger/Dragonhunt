package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.blocks.BlockBreakListener;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
                if (block.getType() == Material.CHEST) {
                    event.setCancelled(true);
                    block.setType(Material.AIR);

                    player.sendMessage(ChatColor.DARK_GRAY + "ᴄʜᴇꜱᴛ ʟᴏᴏᴛ");
                    for (ItemStack loot : GameState.getChestLoot(player)) {
                        block.getWorld().dropItemNaturally(block.getLocation(), loot);
                    }

                    return;
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (block.getType() == Material.ENCHANTING_TABLE
                            || block.getType() == Material.BREWING_STAND
                            || block.getType() == Material.ANVIL) {
                        event.setCancelled(true);
                        player.sendMessage(Prefixes.getErrorPrefix() + "Block disabled.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                        return;
                    }

                    if (BlockBreakListener.woods.contains(block.getType())) { // can not strip wood logs
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
