package com.tharox.dragonhunt.dimensions;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DimensionListener implements Listener {

    private final Location loc;

    public DimensionListener(Location loc) {
        this.loc = loc;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if(player.getWorld().getName().equals("world_nether")) {
            for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 9));
        }
    }

    /**
     * Only affects nether portals.
     * If the player is in the NETHER after the event, teleport him to the selected dungeon entry portal.
     * If the player wants to go from the NETHER to the OVERWORLD by going through a portal
     * check if it's the dungeons entry portal or exit portal.
     * If it is the entry portal, don't search for a available portal.
     * If it is the exit portal, teleport the player to the portal in the OVERWORLD where he came from.
     */
    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location locFrom = event.getFrom();
        World worldFrom = event.getFrom().getWorld();

        if(worldFrom == null) return;

        if(event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCanCreatePortal(false);

            if(worldFrom.getName().equals("world")) {
                player.setBedSpawnLocation(player.getLocation(), true);
                GameState.overworldLocation.put(player, player.getLocation());
                event.setTo(GameState.dungeonLocation);
            }

            if(worldFrom.getName().equals("world_nether")) {
                if(locFrom.distance(GameState.dungeonLocation) < 3) {
                    event.setSearchRadius(0);
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                    player.teleport(GameState.overworldLocation.get(player));
                }
            }
        }
    }
}
