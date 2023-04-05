package com.tharox.dragonhunt.grappler;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class GrappleListener implements Listener {

    public static int maxArrowDistance = 20; // Blocks
    public static int cooldown = 400; // Ticks

    @EventHandler
    public void grappleEvent(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            // AbstractArrow includes normal Arrows, Tipped Arrows and Spectral Arrows
            AbstractArrow arrow = (AbstractArrow) event.getProjectile();
            ItemStack bow = event.getBow();
            if (bow == null) return;

            ItemMeta meta = bow.getItemMeta();
            if (meta == null) return;

            if (!meta.getDisplayName().contains("Grappler")) return;

            arrow.setVelocity(arrow.getVelocity().multiply(2D));

            int cooldownToApply = GrappleListener.cooldown - 20 * GameState.kitCooldownReduction.get(player);

            // put the GRAPPLER on cooldown
            if (cooldownToApply >= 0) {
                player.setCooldown(Material.CROSSBOW, cooldownToApply);
            }

            // after the cooldown ends, add the next charged projectile
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), task -> {
                CrossbowMeta crossbowMeta = (CrossbowMeta) bow.getItemMeta();
                crossbowMeta.addChargedProjectile(new ItemStack(Material.ARROW, 1));
                bow.setItemMeta(crossbowMeta);
            }, 5L);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.getLocation().distance(player.getLocation()) > maxArrowDistance) {
                        arrow.remove();
                        this.cancel();
                    }

                    if (player.isSneaking()) {
                        player.setVelocity(player.getLocation().getDirection().multiply(2D));
                        arrow.remove();
                        this.cancel();
                    }

                    if (arrow.isOnGround() && !arrow.isDead()) {
                        Vector direction = arrow.getLocation().toVector()
                                .subtract(player.getLocation().toVector()).normalize();
                        player.setVelocity(direction.multiply(2D));
                        drawLine(player.getLocation(), arrow.getLocation(), 2);

                        if (player.getLocation().distance(arrow.getLocation()) <= 3) {
                            this.cancel();
                            arrow.remove();
                        }
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 0);
        }
    }

    private void drawLine(Location point1, Location point2, double space) {
        World world = point1.getWorld();

        if (!Objects.equals(point2.getWorld(), world)) { // point2.getWorld().equals(world) produces a warning
            return; // Particles can not be drawn in different worlds.
        }

        double distance = point1.distance(point2);

        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();

        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);

        double covered = 0;

        for (; covered < distance; p1.add(vector)) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.FUCHSIA, 2);
            world.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 1, dustOptions);
            covered += space;
        }
    }
}
