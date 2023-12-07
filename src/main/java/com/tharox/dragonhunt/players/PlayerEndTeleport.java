package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class PlayerEndTeleport implements Listener {

    public PlayerEndTeleport() {
        run();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        World world = player.getWorld();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Enter The End")) {
                event.setCancelled(true); // Do not place the block.

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "Already in the End dimension.");
                    return;
                }

                if (CombatListener.combatList.containsKey(player.getUniqueId())) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "You cannot teleport while in combat.");
                    return;
                }

                // Save the player's current location so the block can be reset later
                // when the player is already in The End.
                Location location = player.getLocation();

                location.getBlock().setType(Material.END_PORTAL);

                Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                        task -> location.getBlock().setType(Material.AIR), 5L);
            }
        }
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Inventory inventory = player.getInventory();

                    // The player never had a END TELEPORTER yet
                    for (ItemStack item : inventory) {
                        if (item == null) continue;

                        // only returns true if both the type and amount of the ItemStack is correct
                        if (item.getType() == Material.ENDER_EYE && item.getAmount() >= getEyesNeeded()) {
                            inventory.remove(item);

                            // replace the Tracker with the END TELEPORTER item
                            inventory.setItem(7, createEndTeleport());
                            player.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + ChatColor.BOLD
                                    + "You can now enter The End.");
                            player.sendMessage(ChatColor.WHITE + "Right-Click" + ChatColor.GRAY + " the "
                                    + ChatColor.LIGHT_PURPLE + "End Portal Frame"
                                    + ChatColor.GRAY + " in your inventory to teleport.");
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    /**
     * Used in the scoreboard.
     *
     * @return the amount of ender eyes needed to be able to enter the End
     */
    public static int getEyesNeeded() {
        int eyesNeeded = 8;

        if (GameState.playersReachedEnd) {
            eyesNeeded = 4;
        } else {
            if (Main.getInstance().getTimer().getTime() >= 900) {
                eyesNeeded = 6;
            }
        }

        return eyesNeeded;
    }

    public static ItemStack createEndTeleport() {
        ItemStack item = new ItemStack(Material.END_PORTAL_FRAME);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Enter The End" + ChatColor.GRAY + " (Right-Click)");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Right-Click: " + ChatColor.LIGHT_PURPLE + "Teleports you to the spawn platform",
                ChatColor.LIGHT_PURPLE + "in the End dimension.",
                "",
                HexColor.RED + "The only way back is dying."));
        item.setItemMeta(meta);

        return item;
    }
}
