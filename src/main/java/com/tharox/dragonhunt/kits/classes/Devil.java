package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@DragonhuntKit
public class Devil extends Kit {
    static int cooldown = 100;

    public static final String title = HexColor.ASSASSIN + KitClass.ASSASSIN + " Devil";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Sets the health of all entities in\n"
            + ChatColor.GRAY + "a radius of " + ChatColor.WHITE + "15" + ChatColor.GRAY + " blocks to "
            + ChatColor.WHITE + "2 ❤" + ChatColor.GRAY + ".\n"
            + HexColor.RED + "The Ender Dragon is immune to this ability.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.WITHER_SKELETON_SKULL;
    public static final int slot = 5;

    public Devil(Player player) {
        super(player);

        KitClass.assassins.add(player);
    }

    @Override
    public void executeAbility() {
        int radius = 15 + Upgrades.Effectiveness.getOrDefault(player, 0);
        int verticalRange = 20;
        int health = 4 - Upgrades.Effectiveness2.getOrDefault(player, 0);

        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        Location playerLocation = player.getLocation();

        World world = playerLocation.getWorld();
        if (world == null) return;

        for (Entity entity : world.getNearbyEntities(playerLocation, radius, radius, radius)) {
            if (entity.getType().equals(EntityType.ENDER_DRAGON)) continue;

            if (entity instanceof LivingEntity && !entity.equals(player)) {
                Location entityLocation = entity.getLocation();

                // Check if the entity is within the vertical range
                if (entityLocation.getY() >= playerLocation.getY() - verticalRange &&
                        entityLocation.getY() <= playerLocation.getY() + verticalRange) {

                    // If the health is already lower than what it would set it to don't do anything
                    if (((LivingEntity) entity).getHealth() > health)
                        ((LivingEntity) entity).setHealth(health);

                    if (entity instanceof Player) {
                        Player p = (Player) entity;
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
                        p.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                                + p.getName() + ChatColor.RED + " set your health to " + HexColor.RED + health + " ❤"
                                + ChatColor.RED + " as a " + title + ChatColor.RED + ".");
                    }
                }
            }
        }
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCooldown() {
        return cooldown - Upgrades.Cooldown.getOrDefault(player, 0);
    }
}
