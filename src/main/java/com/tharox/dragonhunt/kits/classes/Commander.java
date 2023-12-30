package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@DragonhuntKit
public class Commander extends Kit {
    static int duration = 10;
    static int cooldown = 100;

    public static final String title = HexColor.TANK + KitClass.TANK + " Commander";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "All entities in a radius of " + ChatColor.WHITE + "25 Blocks\n"
            + ChatColor.GRAY + "around you get kicked back " + ChatColor.WHITE + "15 Blocks\n"
            + ChatColor.GRAY + "every second and receive " + ChatColor.WHITE + "Weakness" + ChatColor.GRAY + ".\n"
            + HexColor.YELLOW + "» Radius can be increased\n"
            + HexColor.RED + "» Glow and Alert on Activation\n\n"
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
        int boopDistance = 50;

        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                    + player.getName() + ChatColor.GRAY + " uses the " + title + ChatColor.GRAY + " Kit Ability.");
        }

        // Apply Weakness 1 effect to entities in range
        applyWeaknessEffect(radius, duration * 20);

        this.runKitAbility(20, getDuration(), null, () -> {
            boopEntities(radius, boopDistance);
        });
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
                    // Calculate the direction away from the player
                    Vector direction = entityLocation.toVector().subtract(playerLocation.toVector()).normalize();

                    // Boop the entity away from the player by the specified distance
                    entity.setVelocity(direction.multiply(boopDistance / 20.0)); // Adjust the multiplier as needed

                    // Throw the entity up by the specified distance
                    entity.setVelocity(entity.getVelocity().add(new Vector(0, 5 / 10.0, 0)));
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
