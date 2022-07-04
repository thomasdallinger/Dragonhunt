package com.tharox.dragonhunt.vehicles;

import com.tharox.dragonhunt.points.UpgradePoints;
import com.tharox.dragonhunt.upgrades.XPUpgrade;
import org.bukkit.Sound;
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

public class VehicleListener implements Listener {

    /**
     * Remove all items from the minecarts inventory and put a golden apple into it.
     * Then remove the minecart, which drops the inventory.
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (entity.getType() == EntityType.MINECART_CHEST) {
            event.setCancelled(true);
            player.giveExp(UpgradePoints.bonusXP + XPUpgrade.MoreXP.get(player) - 1);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

            StorageMinecart mc = ((StorageMinecart) entity);
            mc.getInventory().clear();
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
