package com.tharox.dragonhunt.arrows;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.upgrades.Upgrades;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class WarperListener implements Listener {
    public static final int COOLDOWN = 400; // Ticks

    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack bow = event.getBow();

            // Check if the Player is on Cooldown
            if (player.getCooldown(Material.BOW) > 0) {
                player.sendMessage(Prefixes.getErrorPrefix() + "Your ability is on cooldown for "
                        + HexColor.RED + (player.getCooldown(Material.BOW) / 20)
                        + ChatColor.RED + " seconds.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                return;
            }

            if (bow != null && bow.hasItemMeta()) {
                ItemMeta meta = bow.getItemMeta();
                if (meta != null && meta.getDisplayName().contains("Warper")) {
                    Arrow arrow = (Arrow) event.getProjectile();
                    arrow.setCustomName("TeleportArrow");

                    // Save the player's yaw and pitch in arrow's metadata
                    arrow.setMetadata("teleportYaw",
                            new FixedMetadataValue(Main.getInstance(), player.getLocation().getYaw()));
                    arrow.setMetadata("teleportPitch",
                            new FixedMetadataValue(Main.getInstance(), player.getLocation().getPitch()));

                    // Put the Bow on cooldown
                    if ((COOLDOWN - Upgrades.Cooldown.getOrDefault(player, 0)) >= 0) {
                        player.setCooldown(Material.BOW,
                                (COOLDOWN - Upgrades.Cooldown.getOrDefault(player, 0)));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            if ("TeleportArrow".equals(arrow.getCustomName()) && arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                Location teleportLocation = arrow.getLocation();

                // Retrieve the saved yaw and pitch from arrow's metadata
                float yaw = (float) arrow.getMetadata("teleportYaw").get(0).value();
                float pitch = (float) arrow.getMetadata("teleportPitch").get(0).value();
                teleportLocation.setYaw(yaw);
                teleportLocation.setPitch(pitch);

                player.teleport(teleportLocation);
                player.sendMessage(HexColor.GREEN + "You have been teleported to your arrow.");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                arrow.remove(); // Remove the arrow after teleportation
            }
        }
    }
}
