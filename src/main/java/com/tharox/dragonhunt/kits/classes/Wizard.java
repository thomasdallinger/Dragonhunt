package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Wizard extends Kit {
    static int duration = 25;
    static int cooldown = 90;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Wizard";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "All entities in a radius of " + ChatColor.WHITE + "25 Blocks\n"
            + ChatColor.GRAY + "around you start " + ChatColor.WHITE + "levitating\n"
            + ChatColor.GRAY + "and receive " + ChatColor.WHITE + "Poison" + ChatColor.GRAY + ".\n"
            + HexColor.YELLOW + "» Levitation Effect can be increased\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.BREWING_STAND;
    public static final int slot = 22;

    public Wizard(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    public void executeAbility() {
        int radius = 25;
        int verticalRange = 20;
        int levitationLevel = Upgrades.Effectiveness.getOrDefault(player, 1);

        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        Location playerLocation = player.getLocation();

        World world = playerLocation.getWorld();
        if (world == null) return;

        for (Entity entity : world.getNearbyEntities(playerLocation, radius, radius, radius)) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                Location entityLocation = entity.getLocation();

                // Check if the entity is within the vertical range
                if (entityLocation.getY() >= playerLocation.getY() - verticalRange &&
                        entityLocation.getY() <= playerLocation.getY() + verticalRange) {

                    ((LivingEntity) entity).addPotionEffect(
                            new PotionEffect(PotionEffectType.LEVITATION, duration * 20,
                                    levitationLevel - 1)
                    );

                    ((LivingEntity) entity).addPotionEffect(
                            new PotionEffect(PotionEffectType.POISON, duration * 20, 0)
                    );
                }
            }
        }

        this.runKitAbility(20, getDuration(), null, null);
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
