package com.tharox.dragonhunt.respawn;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.db.DbValues;
import com.tharox.dragonhunt.players.CombatListener;
import com.tharox.dragonhunt.players.PlayerDeathListener;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class SetRespawn implements Listener {

    public SetRespawn() {
        run();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        World world = player.getWorld();

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;

        EquipmentSlot e = event.getHand(); //Get the hand of the event and set it to 'e'.
        if (e == null)
            return;

        if (e.equals(EquipmentSlot.HAND)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.getItemMeta().getDisplayName().contains("Set Respawn Point")) {
                event.setCancelled(true); // Avoid block placement.

                if (world.getName().equals("world_the_end")) {
                    player.sendMessage(Prefixes.getErrorPrefix()
                            + ChatColor.GRAY + "Respawn point cannot be set in the End dimension.");
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

                if (CombatListener.combatList.containsKey(player.getUniqueId())) {
                    player.sendMessage(Prefixes.getErrorPrefix() + "You cannot set your respawn point while in combat.");
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
                player.sendMessage(Prefixes.getImportantPrefix() + HexColor.RED + "-5 ❤");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            }
        }
    }

    public void sendActionBar(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        int distance;

        if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

        if (item.getItemMeta().getDisplayName().contains("Set Respawn Point")) {
            try {
                if (PlayerDeathListener.deathLocation.containsKey(player)) {
                    distance = (int) player.getLocation().distance(PlayerDeathListener.deathLocation.get(player));

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder("LAST DEATH").color(HexColor.RED).bold(true)
                                    .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                    .append(String.valueOf(distance)).color(ChatColor.GRAY)
                                    .append(" Blocks away").color(ChatColor.GRAY).create());
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new ComponentBuilder("LAST DEATH").color(HexColor.RED).bold(true)
                                    .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                    .append("0 ✟").color(ChatColor.RED).create());
                }
            } catch (IllegalArgumentException ex) {
                // Different dimension.
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new ComponentBuilder("LAST DEATH").color(ChatColor.GOLD).bold(true)
                                .append(" » ").color(ChatColor.DARK_GRAY).bold(false)
                                .append(DbValues.getCurrentWorld(PlayerDeathListener.deathLocation.get(player)))
                                .color(HexColor.RED).create());
            }
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

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Set Respawn Point" + ChatColor.GRAY + " (Right-Click)");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Sets your respawn point",
                ChatColor.GRAY + "at your current location.",
                ChatColor.GRAY + "Costs " + ChatColor.WHITE + "5 ❤" + ChatColor.GRAY + "."));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }
}
