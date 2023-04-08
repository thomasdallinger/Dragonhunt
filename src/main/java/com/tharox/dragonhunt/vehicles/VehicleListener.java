package com.tharox.dragonhunt.vehicles;

import com.tharox.dragonhunt.coins.Coins;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

public class VehicleListener implements Listener {

    /**
     * Remove all items from the minecarts inventory. When a player clicks a minecart it disappears.
     * The player receives XP from clicking on the minecart.
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (entity.getType() == EntityType.MINECART_CHEST) {
            event.setCancelled(true);

            StorageMinecart mc = ((StorageMinecart) entity);
            mc.getInventory().clear();

            player.sendMessage(ChatColor.DARK_GRAY + "ᴍɪɴᴇᴄᴀʀᴛ ʟᴏᴏᴛ");
            for (ItemStack loot : GameState.getChestLoot(player)) {
                mc.getInventory().addItem(loot);
            }

            entity.remove();
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();

        if (vehicle.getType() == EntityType.MINECART_CHEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroyed(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();

        if (vehicle.getType() == EntityType.MINECART_CHEST) {
            event.setCancelled(true);
        }
    }
}
