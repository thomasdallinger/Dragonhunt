package com.tharox.dragonhunt.players;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StructureSearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class PlayerTeleport implements Listener {

    private static HashMap<Player, Boolean> gotBastionTeleport = new HashMap<>();

    public PlayerTeleport() {
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
                player.sendTitle("", HexColor.GREEN + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", 0, 30, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);

                Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                        task -> location.getBlock().setType(Material.AIR), 5L);

            } else if (item.getItemMeta().getDisplayName().contains("Enter Bastion")) {
                event.setCancelled(true); // Do not place the block.

                if (!world.getEnvironment().equals(World.Environment.NETHER)) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "You need to be in the Nether dimension.");
                    return;
                }

                if (CombatListener.combatList.containsKey(player.getUniqueId())) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "You cannot teleport while in combat.");
                    return;
                }

                StructureSearchResult structureSearchResult = world.locateNearestStructure(
                        player.getLocation(), Structure.BASTION_REMNANT, 32, false);

                if (structureSearchResult == null) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "No Bastion found.");
                    return;
                }

                // Remove the teleporter, replace with Scout item
                player.getInventory().remove(Material.GILDED_BLACKSTONE);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                        task -> player.getInventory().setItem(7, GameState.getScoutItem()), 20L);

                player.teleport(new Location(world,
                        structureSearchResult.getLocation().getX(),
                        100,
                        structureSearchResult.getLocation().getZ()));
                player.sendTitle("", HexColor.GREEN + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", 0, 30, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
            }
        }
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Inventory inventory = player.getInventory();
                    boolean hasEnoughEyes;
                    boolean hasEnoughPowder = inventory.contains(Material.BLAZE_POWDER, getEyesNeeded());
                    Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("nether/find_bastion"));

                    // Give the player a Bastion Teleporter if he has never got one
                    if (hasEnoughPowder && !gotBastionTeleport.getOrDefault(player, false)) {
                        // Replace the Scout with the BASTION TELEPORTER item
                        inventory.setItem(7, createBastionTeleport());
                        gotBastionTeleport.put(player, true);
                        player.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + ChatColor.BOLD
                                + "You can now enter a Bastion.");
                        player.sendMessage(ChatColor.WHITE + "Right-Click" + ChatColor.GRAY + " the "
                                + ChatColor.LIGHT_PURPLE + "Gilded Blackstone"
                                + ChatColor.GRAY + " in your inventory to teleport.");

                        player.sendTitle("", ChatColor.LIGHT_PURPLE
                                + "ᴛᴇʟᴇᴘᴏʀᴛ ᴀᴠᴀɪʟᴀʙʟᴇ", 0, 30, 10);
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    }

                    // The player never had a END TELEPORTER yet
                    if (inventory.getItem(7) != null
                            && inventory.getItem(7).getType().equals(Material.ENDER_EYE)) {
                        // The Scout item is an additional Ender Eye in the player's inventory
                        hasEnoughEyes = inventory.contains(Material.ENDER_EYE, getEyesNeeded() + 1);
                    } else {
                        hasEnoughEyes = inventory.contains(Material.ENDER_EYE, getEyesNeeded());
                    }

                    if (hasEnoughEyes) {
                        // Remove all Ender Eyes
                        inventory.remove(Material.ENDER_EYE);

                        // Replace the Scout with the END TELEPORTER item
                        inventory.setItem(7, createEndTeleport());
                        player.sendMessage(Prefixes.getImportantPrefix() + HexColor.GREEN + ChatColor.BOLD
                                + "You can now enter The End.");
                        player.sendMessage(ChatColor.WHITE + "Right-Click" + ChatColor.GRAY + " the "
                                + ChatColor.LIGHT_PURPLE + "End Portal Frame"
                                + ChatColor.GRAY + " in your inventory to teleport.");

                        player.sendTitle("", ChatColor.LIGHT_PURPLE
                                + "ᴛᴇʟᴇᴘᴏʀᴛ ᴀᴠᴀɪʟᴀʙʟᴇ", 0, 30, 10);
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
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
                ChatColor.WHITE + "Teleports" + ChatColor.GRAY + " you to the spawn platform",
                ChatColor.GRAY + "in the End dimension.",
                "",
                HexColor.RED + "The only way back is dying."));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createBastionTeleport() {
        ItemStack item = new ItemStack(Material.GILDED_BLACKSTONE);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Enter Bastion" + ChatColor.GRAY + " (Right-Click)");
        meta.setLore(Collections.singletonList(
                ChatColor.WHITE + "Teleports" + ChatColor.GRAY + " you to the nearest Bastion."));
        item.setItemMeta(meta);

        return item;
    }
}
