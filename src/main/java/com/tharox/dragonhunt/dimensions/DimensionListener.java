package com.tharox.dragonhunt.dimensions;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.EnderDragonDamageListener;
import com.tharox.dragonhunt.respawn.RespawnAnchor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StructureSearchResult;

import java.util.Random;

public class DimensionListener implements Listener {
    /**
     * Give players Resistance 10 when changing worlds so they can't be damaged for the first 7 seconds.
     * Removes the Tracker from a player's inventory in the END. Returns the Tracker when changing to a different
     * world again.
     * Updated the item material for the respawn item.
     * If the player was in the End already, the slot remains free to use.
     */
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 9));

        if (!event.getFrom().getEnvironment().equals(World.Environment.THE_END)) {
            player.getInventory().setItem(6, RespawnAnchor.createItem(player));
        }
    }

    /**
     * Only affects nether and end portals.
     * <p>
     * Once the first player has reached the NETHER, all other players will be teleported to that
     * NETHER portal when entering the NETHER.
     * Players can exit the NETHER by either using that portal or a different one.
     * <p>
     * Note: Portals will only be created inside world borders.
     */
    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        World worldFrom = event.getFrom().getWorld();
        Location eventTo = event.getTo();
        Player player = event.getPlayer();

        if (worldFrom == null) return;
        if (eventTo == null) return;

        World worldTo = eventTo.getWorld();

        if (worldTo == null) return;
        if (worldTo.getEnvironment().equals(World.Environment.NORMAL)) return;

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (worldFrom.getName().equals("world")) {

                // No player has reached the NETHER before.
                // Set the first NETHER PORTAL close to a fortress/bastion.
                if (GameState.firstNetherEntry == null) {
                    Random random = new Random();
                    int number = random.nextInt(2);
                    StructureSearchResult structureSearchResult;

                    if (number == 0) {
                        structureSearchResult = worldTo.locateNearestStructure(
                                player.getLocation(), Structure.FORTRESS, 32, false);
                    } else {
                        structureSearchResult = worldTo.locateNearestStructure(
                                player.getLocation(), Structure.BASTION_REMNANT, 32, false);
                    }

                    if (structureSearchResult != null) {
                        event.setCanCreatePortal(true);
                        event.setTo(structureSearchResult.getLocation());
                    }
                } else {
                    event.setCanCreatePortal(false);
                    event.setTo(GameState.firstNetherEntry);
                }
            }
        }

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            // 15 Second delay
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                EnderDragonDamageListener.timeLeft.put(player.getUniqueId(), EnderDragonDamageListener.time);
                EnderDragonDamageListener.bossBar.addPlayer(player);
            }, 300L);
        }
    }
}
