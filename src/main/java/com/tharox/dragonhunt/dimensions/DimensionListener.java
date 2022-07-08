package com.tharox.dragonhunt.dimensions;

import com.tharox.dragonhunt.game.GameState;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DimensionListener implements Listener {
    /**
     * Give players Resistance 10 when changing worlds so they can't be damaged for the first 7 seconds.
     */
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 9));
    }

    /**
     * Only affects nether portals.
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

        if (worldFrom == null) return;
        if (GameState.firstNetherEntry == null) return; // no player has reached the NETHER before

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (worldFrom.getName().equals("world")) {
                event.setCanCreatePortal(false);
                event.setTo(GameState.firstNetherEntry);
            }
        }
    }
}
