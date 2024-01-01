package com.tharox.dragonhunt.respawn;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.players.CombatListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RespawnAnchor implements Listener {

    private List<Player> playerUsesTeleport = new ArrayList<>();

    public RespawnAnchor() {
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
                && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Respawn Anchor")) {
                event.setCancelled(true); // Avoid block placement.

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "Respawn point cannot be set in the End dimension.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                if (CombatListener.combatList.containsKey(player.getUniqueId())) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "You cannot set your respawn point while in combat.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                // Player has to have more than 5 hearts.
                // Players lose 5 hearts upon setting the respawn point.
                if (player.getHealth() < 10.0) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "You need to have more than 5 ❤ " +
                            "to set your respawn point.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                player.setBedSpawnLocation(player.getLocation(), true);
                player.setHealth(player.getHealth() - 10.0);
                player.sendMessage(HexColor.GREEN + "Respawn Point set at "
                        + ChatColor.DARK_GREEN + "[" + DbValues.getCurrentWorld(player.getLocation())
                        + ", x: " + (int) player.getLocation().getX()
                        + ", y: " + (int) player.getLocation().getY()
                        + ", z: " + (int) player.getLocation().getZ() + "]"
                        + HexColor.GREEN + ".");
                player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED + "-5 ❤ Penalty");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            }

            return; // if it was Left-Click, no need to check Right-Click
        }

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Respawn Anchor")) {
                event.setCancelled(true); // Avoid block placement.

                if (playerUsesTeleport.contains(player)) {
                    player.sendMessage(HexColor.RED + "Cancelling teleportation...");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    playerUsesTeleport.remove(player);
                    return;
                }

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "This feature is disabled in the End dimension.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                if (CombatListener.combatList.containsKey(player.getUniqueId())) {
                    player.sendMessage(Prefixes.getErrorPrefix()
                            + ChatColor.GRAY + "You cannot teleport while in combat.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                // Player has to have more than 5 hearts.
                // Players lose 5 hearts upon setting the respawn point.
                if (player.getHealth() < 10.0) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "You need to have more than 5 ❤ " +
                            "to teleport to your respawn point.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    return;
                }

                int delay = 4;

                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, delay * 20, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, delay * 20, 9));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, delay * 20,
                        9));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, delay * 20, 200));

                player.sendMessage(HexColor.GREEN + "Teleporting in " + ChatColor.DARK_GREEN + delay
                        + HexColor.GREEN + " seconds.");
                player.sendMessage(ChatColor.WHITE + "Right-Click again" + ChatColor.GRAY + " to cancel the teleport.");
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1, 1);
                playerUsesTeleport.add(player);

                Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                    if (playerUsesTeleport.contains(player)) {

                        if (player.getBedSpawnLocation() != null) {
                            player.teleport(player.getBedSpawnLocation());
                        } else {
                            player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
                        }

                        player.setHealth(player.getHealth() - 10.0);

                        player.sendMessage(HexColor.GREEN + "Teleported to your Respawn Point.");
                        player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED + "-5 ❤ Penalty");
                        player.sendTitle("", HexColor.GREEN + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", 0, 30, 10);

                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);

                        playerUsesTeleport.remove(player);
                    }
                }, delay * 20L);
            }
        }
    }

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        if (item.getItemMeta().getDisplayName().contains("Respawn Anchor")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new ComponentBuilder("SET RESPAWN").color(HexColor.YELLOW).bold(true)
                            .append(" (Left-Click)").color(ChatColor.GRAY).bold(false)
                            .append(" | ").color(ChatColor.DARK_GRAY)
                            .append("TP TO RESPAWN").color(HexColor.YELLOW).bold(true)
                            .append(" (Right-Click)").color(ChatColor.GRAY).bold(false).create());
        }
    }

    protected void run() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendActionBar(player);
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    public static ItemStack createItem(Player player) {
        ItemStack item = new ItemStack(Material.AIR);

        if (!(player.getWorld().getEnvironment() == World.Environment.THE_END)) {
            item = new ItemStack(Material.RESPAWN_ANCHOR);
        } else {
            return item;
        }

        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawn Anchor");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Set a new respawn point",
                ChatColor.GRAY + "at your current location",
                ChatColor.GRAY + "or teleport to it.",
                ChatColor.GRAY + "Deals " + ChatColor.WHITE + "5 ❤" + ChatColor.GRAY + " Damage.",
                "",
                ChatColor.GRAY + "Left-Click: " + HexColor.YELLOW + "Set Respawn Point",
                ChatColor.GRAY + "Right-Click: " + HexColor.YELLOW + "TP to Respawn Point",
                ChatColor.GRAY + "Right-Click again: " + HexColor.RED + "Cancel TP"));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }
}
