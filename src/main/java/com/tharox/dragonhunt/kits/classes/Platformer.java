package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.kits.upgrades.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Platformer extends Kit {
    static int duration = 16;
    static int cooldown = 70;

    public static final String title = HexColor.MOBILITY + KitClass.MOBILITY + " Platformer";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Every second blocks will spawn below you\n"
            + ChatColor.GRAY + "making it possible to walk everywhere. \n"
            + ChatColor.GRAY + "You can go up, down and through (most) blocks. \n"
            + ChatColor.GRAY + "You receive " + ChatColor.WHITE + "Speed 3" + ChatColor.GRAY + ".\n"
            + HexColor.RED + "» Glow and Alert on Activation\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.PINK_STAINED_GLASS;
    public static final int slot = 30;

    public Platformer(Player player) {
        super(player);

        KitClass.mobilities.add(player);
    }

    @Override
    public void executeAbility() {
        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, getDuration() * 20, 0));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GRAY
                    + " uses the " + title + ChatColor.GRAY + " Kit Ability.");
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getDuration() * 20, 1));

        this.runKitAbility(5, getDuration(), null, () -> {
            setBlocksBelowPlayer(player);
        });
    }

    public void setBlocksBelowPlayer(Player player) {
        Location playerLocation = player.getLocation();
        Location offsetLocation;

        for (int offsetY = -1; offsetY != 3; offsetY++) {
            Material material = Material.AIR;

            if (offsetY == -1) {
                material = Material.PINK_STAINED_GLASS;
            }

            for (int offsetX = -1; offsetX != 2; offsetX++) {
                for (int offsetZ = -1; offsetZ != 2; offsetZ++) {
                    offsetLocation = new Location(playerLocation.getWorld(),
                            playerLocation.getX() + offsetX,
                            playerLocation.getY() + offsetY,
                            playerLocation.getZ() + offsetZ);
                    if (offsetLocation.getBlock().getType() != Material.OBSIDIAN
                            && offsetLocation.getBlock().getType() != Material.BEDROCK
                            && offsetLocation.getBlock().getType() != Material.SPAWNER) {
                        offsetLocation.getBlock().setType(material);
                    }
                }
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