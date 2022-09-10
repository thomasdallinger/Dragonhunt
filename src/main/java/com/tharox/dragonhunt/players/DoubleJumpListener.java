package com.tharox.dragonhunt.players;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;
import java.util.List;

public class DoubleJumpListener implements Listener {
    public static List<String> playersAllowedToDoubleJump = new ArrayList<>();

    @EventHandler
    public void setFly(PlayerJoinEvent event) {
        event.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SPECTATOR) {
            event.setCancelled(true);

            if (!playersAllowedToDoubleJump.contains(player.getName())) return;

            player.setAllowFlight(false);
            player.setFlying(false);
            player.setVelocity(player.getLocation().getDirection().multiply(0.5D).setY(0.75D));
            player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!playersAllowedToDoubleJump.contains(player.getName()) || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            player.setAllowFlight(true);
        }
    }
}
