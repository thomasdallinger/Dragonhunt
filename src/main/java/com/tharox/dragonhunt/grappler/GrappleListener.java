package com.tharox.dragonhunt.grappler;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.game.GameState;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GrappleListener implements Listener {

    @EventHandler
    public void grappleEvent(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Arrow arrow = (Arrow) event.getProjectile();
            ItemStack bow = event.getBow();
            if (bow == null) return;

            ItemMeta meta = bow.getItemMeta();
            if (meta == null) return;

            if (!meta.getDisplayName().equals(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Grappler")) return;
            if (player.getWorld().getName().equals("world_nether") && !GameState.minOnePlayerInEnd) return;

            arrow.setVelocity(arrow.getVelocity().multiply(2D));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.getLocation().distance(player.getLocation()) > 20) {
                        arrow.remove();
                        player.sendMessage(ChatColor.RED + "Too far away!");
                        this.cancel();
                    }

                    if (player.isSneaking()) {
                        player.setVelocity(player.getLocation().getDirection().multiply(2D));
                        arrow.remove();
                        this.cancel();
                    }

                    if (arrow.isOnGround() && !arrow.isDead()) {
                        Vector direction = arrow.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
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
        Validate.isTrue(point2.getWorld().equals(world), "Particles can not be drawn in different worlds.");

        double distance = point1.distance(point2);

        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();

        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);

        double covered = 0;

        for (; covered < distance; p1.add(vector)) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.YELLOW, 1);
            world.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 1, dustOptions);
            covered += space;
        }
    }
}
