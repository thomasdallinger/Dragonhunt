package com.tharox.dragonhunt.deathlisteners;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.points.Points;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerDeathListener implements Listener {

    /**
     * The player always keeps:
     * - hotbar (9 items, including tracker and kit ability items)
     * - offhand (1 item)
     * - armor (4 items, 3 items if the player had an elytra)
     * - all XP
     * <p>
     * The player always looses:
     * - half of his points the player had at the time of dying
     * <p>
     * If the player had been in the nether, the player respawns in the nether.
     */
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        int points = Points.getPoints(player) / 2;

        Points.removePoints(player, points);

        points += 100;
        if (killer != null) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
            killer.sendMessage(ChatColor.AQUA + "You received " + ChatColor.DARK_AQUA + points + ChatColor.AQUA + " Points by killing "
                    + ChatColor.DARK_AQUA + player.getName() + ChatColor.AQUA + ".");

            Points.addPoints(killer, points);
        }

        ItemStack itemToRemove;
        for (int index = 9; index <= 35; index++) {
            itemToRemove = player.getInventory().getItem(index);

            if (itemToRemove != null) {
                player.getInventory().remove(itemToRemove);
                player.getWorld().dropItemNaturally(player.getLocation(), itemToRemove);
            }
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            player.getInventory().setChestplate(new ItemStack(Material.AIR));
        }

        event.setDroppedExp(0);
        event.getDrops().clear();

        GameState.itemsToKeepOnDeath.put(player, player.getInventory().getContents());

        player.sendMessage(ChatColor.GRAY + "You died at " + ChatColor.GOLD + ChatColor.BOLD + (int) player.getLocation().getX()
                + ", " + (int) player.getLocation().getY()
                + ", " + (int) player.getLocation().getZ()
                + ", " + player.getWorld().getName());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
            Player player = event.getPlayer();

            player.getInventory().setContents(GameState.itemsToKeepOnDeath.get(player));

            player.sendMessage(ChatColor.GOLD + "You respawned with your Hotbar, Offhand and Armor Items.");
            player.sendMessage(ChatColor.RED + "You now have " + ChatColor.DARK_RED + Points.getPoints(player)
                    + ChatColor.RED + " Points left.");

        }, 5L);
    }
}
