package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Platformer extends Kit {
    static int duration = 20;
    static int cooldown = 180;

    public static final String title = HexColor.MOBILITY + KitClass.MOBILITY + " Platformer";
    public static final String lore = ChatColor.DARK_GRAY.toString()
            + ChatColor.ITALIC + "Walk Anywhere\n\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you receive " + ChatColor.WHITE + "Speed 3" + ChatColor.GRAY + ".\n"
            + ChatColor.GRAY + "Every second blocks will spawn below you \n"
            + ChatColor.GRAY + "making it possible to walk everywhere. \n"
            + ChatColor.GRAY + "You can go up, down and through walls. \n"
            + ChatColor.GRAY + "You will be glowing while the ability is active.\n\n"
            + ChatColor.GRAY + "Duration: " + ChatColor.WHITE + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.PINK_STAINED_GLASS;
    public static final int slot = 30;

    public Platformer(Player player) {
        super(player);

        KitClass.mobilities.add(player);
    }

    @Override
    public void executeAbility() {
        GameState.actionBarNeededForKitAbility.put(player, true);
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));

        this.runKitAbility(2, duration, null, () -> {
            setBlocksBelowPlayer(player);
        });
    }

    public void setBlocksBelowPlayer(Player player) {
        Location playerLocation = player.getLocation();
        Location offsetLocation;

        for (int offsetY = -1; offsetY != 2; offsetY++) {
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
                            && offsetLocation.getBlock().getType() != Material.BEDROCK) {
                        offsetLocation.getBlock().setType(material);
                    }
                }
            }
        }
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}