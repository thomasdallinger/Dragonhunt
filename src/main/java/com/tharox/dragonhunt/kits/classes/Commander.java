package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.Main;
import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@DragonhuntKit
public class Commander extends Kit {
    static int duration = 10;
    static int cooldown = 50;
    static int boopInterval = 40; // 20 ticks per second, so 40 ticks for every 2 seconds
    private BukkitRunnable boopTask;

    public static final String title = HexColor.TANK + KitClass.TANK + " Commander";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "All entities in a radius of " + ChatColor.WHITE + "25 Blocks\n"
            + ChatColor.GRAY + "around you get kicked back " + ChatColor.WHITE + "15 Blocks\n"
            + ChatColor.GRAY + "every " + ChatColor.WHITE + "2 seconds"
            + ChatColor.GRAY + " and receive " + ChatColor.WHITE + "Weakness" + ChatColor.GRAY + ".\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.IRON_HELMET;
    public static final int slot = 13;

    public Commander(Player player) {
        super(player);

        KitClass.tanks.add(player);
    }

    @Override
    public void executeAbility() {
        int radius = 25 + Upgrades.Effectiveness.getOrDefault(player, 0);
        int boopDistance = 15;
        int weaknessDuration = duration * 20; // Convert seconds to ticks

        // Schedule the repeating task to boop entities every 2 seconds
        boopTask = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;

                if (ticks % boopInterval == 0) {
                    boopEntities(radius, boopDistance);
                }

                if (ticks >= duration * 20) {
                    // Cancel the task when the duration is over
                    cancel();
                }
            }
        };

        boopTask.runTaskTimer(Main.getInstance(), 0, 1);

        // Apply Weakness 1 effect to entities in range
        applyWeaknessEffect(radius, weaknessDuration);
    }

    private void boopEntities(int radius, int boopDistance) {
        Location playerLocation = player.getLocation();
        World world = playerLocation.getWorld();
        if (world == null) return;

        for (Entity entity : world.getNearbyEntities(playerLocation, radius, radius, radius)) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                Location entityLocation = entity.getLocation();

                // Check if the entity is within the specified range
                if (entityLocation.distanceSquared(playerLocation) <= boopDistance * boopDistance) {
                    // Boop the entity away from the player
                    Vector direction = entityLocation.toVector().subtract(playerLocation.toVector()).normalize();
                    entity.setVelocity(direction.multiply(1.5)); // Adjust the multiplier as needed
                }
            }
        }
    }

    private void applyWeaknessEffect(int radius, int duration) {
        Location playerLocation = player.getLocation();
        World world = playerLocation.getWorld();
        if (world == null) return;

        for (Entity entity : world.getNearbyEntities(playerLocation, radius, radius, radius)) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                // Apply Weakness 1 effect to the entity
                ((LivingEntity) entity).addPotionEffect(
                        new PotionEffect(PotionEffectType.WEAKNESS, duration, 0)
                );
            }
        }
    }

    @Override
    public int getDuration() {
        return duration + Upgrades.Duration.getOrDefault(player, 0);
    }

    @Override
    public int getCooldown() {
        return cooldown - Upgrades.Cooldown.getOrDefault(player, 0);
    }
}
