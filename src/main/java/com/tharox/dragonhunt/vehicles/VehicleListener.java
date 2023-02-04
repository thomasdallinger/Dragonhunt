package com.tharox.dragonhunt.vehicles;

import com.tharox.dragonhunt.coins.Coins;
import org.bukkit.Material;
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
            Coins.addXP(player, Coins.bonusXP);

            StorageMinecart mc = ((StorageMinecart) entity);
            mc.getInventory().clear();
            mc.getInventory().addItem(
                    new ItemStack(Material.DIAMOND, 2),
                    new ItemStack(Material.GOLD_INGOT, 4),
                    new ItemStack(Material.IRON_INGOT, 4),
                    new ItemStack(Material.APPLE, 1));
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
