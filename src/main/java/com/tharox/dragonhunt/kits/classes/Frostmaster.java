package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@DragonhuntKit
public class Frostmaster extends Kit {
    static int radius = 10;
    static int cooldown = 60;
    private static final Material domeMaterial = Material.PACKED_ICE;

    public static final String title = HexColor.TANK + KitClass.TANK + " Frostmaster";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Create a double-layered ice dome in the\n"
            + ChatColor.GRAY + "radius of " + ChatColor.WHITE + ChatColor.WHITE + radius
            + ChatColor.GRAY + " blocks around yourself.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.PACKED_ICE;
    public static final int slot = 14;

    public Frostmaster(Player player) {
        super(player);

        KitClass.tanks.add(player);
    }

    @Override
    public void executeAbility() {
        int additionalRadius = Upgrades.Effectiveness.getOrDefault(player, 0);
        radius += additionalRadius;
        int layers = 2 + Upgrades.Effectiveness2.getOrDefault(player, 0);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                    + player.getName() + ChatColor.GRAY + " uses the " + title + ChatColor.GRAY + " Kit Ability.");
        }

        Vector center = player.getLocation().toVector();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Vector position = center.clone().add(new Vector(x, y, z));
                    double distance = center.distance(position);

                    if (distance <= radius && distance > radius - layers) {
                        Block block = player.getWorld().getBlockAt(position.toLocation(player.getWorld()));
                        if (block.getType() != Material.BEDROCK && block.getType() != Material.OBSIDIAN) {
                            block.setType(domeMaterial);
                        }
                    }
                }
            }
        }

        player.sendMessage(HexColor.GREEN + "You have created an ice dome.");
        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
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
