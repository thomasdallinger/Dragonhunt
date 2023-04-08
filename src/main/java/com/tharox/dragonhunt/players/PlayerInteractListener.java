package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.blocks.BlockBreakListener;
import com.tharox.dragonhunt.brewing.BrewingInventory;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.db.DatabaseExecutor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.enchanting.EnchantingInventory;
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

import java.sql.Statement;

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
                    // clicking on an enchanting table opens the enchant inventory
                    if (block.getType() == Material.ENCHANTING_TABLE) {
                        event.setCancelled(true);

                        EnchantingInventory enchantingInventory
                                = new EnchantingInventory(Main.getInstance().getConfig(), player);
                        player.openInventory(enchantingInventory.enchant);
                        return;
                    }

                    // clicking on a brewing stand opens the brewing stand inventory
                    if (block.getType() == Material.BREWING_STAND) {
                        event.setCancelled(true);

                        BrewingInventory brewingInventory
                                = new BrewingInventory(Main.getInstance().getConfig(), player);
                        player.openInventory(brewingInventory.menu);
                        return;
                    }

                    // clicking on an anvil reduces the Kit cooldown
                    if (block.getType() == Material.ANVIL) {
                        event.setCancelled(true);
                        int cost = 10;
                        int reduction = 5;
                        String name = "Kit Cooldown Reduction";

                        GameState.kitCooldownReduction.put(player,
                                GameState.kitCooldownReduction.get(player) + reduction);

                        // ERROR: not enough points to purchase a cooldown reduction
                        if (Coins.getCoins(player) < cost) {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                            player.sendMessage(Prefixes.getErrorPrefix() + "You do not have enough ⛃! Need "
                                    + HexColor.RED + (cost - Coins.getCoins(player))
                                    + ChatColor.RED + " more.");
                            return;
                        }

                        Coins.removeCoins(player, cost);
                        player.sendMessage(Prefixes.getCoinPrefix(-cost)
                                + ChatColor.GRAY + "Purchased " + HexColor.YELLOW + name + ChatColor.GRAY + ".");
                        player.sendMessage(HexColor.GREEN + "Kit Cooldown reduced by "
                                + ChatColor.DARK_GREEN + reduction + HexColor.GREEN + " seconds.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);

                        DatabaseExecutor.executeDatabaseHandler((con) -> {
                            String query = "INSERT INTO purchases (game_id, player, purchased_item, coins_before, cost, timestamp) " +
                                    "VALUES ('" + DbValues.getGameID() + "', '" + player.getName() + "', '" + name
                                    + "', '" + (Coins.getCoins(player) + cost)
                                    + "', '" + cost
                                    + "', '" + DbValues.getTimestamp() + "')";

                            Statement stmt = con.createStatement();
                            stmt.execute(query);
                            stmt.close();
                        });
                    }

                    if (BlockBreakListener.woods.contains(block.getType())) { // can not strip wood logs
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
