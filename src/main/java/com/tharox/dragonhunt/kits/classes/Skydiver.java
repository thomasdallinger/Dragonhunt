package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.upgrades.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@DragonhuntKit
public class Skydiver extends Kit {
    static int blocksAbove = 15;
    static int cooldown = 30;

    public static final String title = HexColor.FLANKER + KitClass.FLANKER + " Skydiver";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Teleport " + ChatColor.WHITE + blocksAbove + ChatColor.GRAY + " blocks up (if possible).\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.ELYTRA;
    public static final int slot = 32;

    public Skydiver(Player player) {
        super(player);

        KitClass.flankers.add(player);
    }

    @Override
    public void executeAbility() {
        Location location = player.getLocation();
        Location locationToTeleportTo = new Location(player.getWorld(), location.getX(), location.getY()
                + (blocksAbove + Upgrades.Effectiveness.getOrDefault(player, 0)), location.getZ());

        if (isSafeToTeleport(locationToTeleportTo)) {
            player.teleport(locationToTeleportTo);
            player.playSound(locationToTeleportTo, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            player.sendMessage(title + ChatColor.GRAY + " Kit Ability used.");
        } else {
            player.sendMessage(Prefixes.getErrorPrefix() + "It's not safe to teleport there.");
            player.playSound(locationToTeleportTo, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }
    }

    public boolean isSafeToTeleport(Location location) {
        // Get the block at the target location and the one above it
        Material blockAtLocation = location.getBlock().getType();
        Material blockAboveLocation = location.clone().add(0, 1, 0).getBlock().getType();

        // Check if both blocks are air (or another non-solid block)
        return blockAtLocation == Material.AIR && blockAboveLocation == Material.AIR;
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

