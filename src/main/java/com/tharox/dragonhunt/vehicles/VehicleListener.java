package com.tharox.dragonhunt.vehicles;

import com.tharox.dragonhunt.points.GearPoints;
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
     * Remove all items from the minecarts inventory and put a golden apple into it.
     * Then remove the minecart, which drops the inventory.
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.MINECART_CHEST) {
            event.setCancelled(true);

            StorageMinecart mc = ((StorageMinecart) entity);
            mc.getInventory().clear();
            mc.getInventory().addItem(
                    new ItemStack(Material.IRON_INGOT, 8),
                    new ItemStack(Material.GOLD_INGOT, 6));
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
