package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Demolitionist extends Kit {
    static int duration = 25;
    static int cooldown = 120;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Demolitionist";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation " + ChatColor.WHITE + "4" + ChatColor.GRAY + " layers of "
            + ChatColor.WHITE + "TNT\n"
            + ChatColor.GRAY + "will spawn below you.\n"
            + ChatColor.GRAY + "During the explosion you are invulnerable\n"
            + ChatColor.GRAY + "to damage and you cannot move.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n"
            + HexColor.YELLOW + "⇣ Cooldown can be reduced\n\n";
    public static final Material material = Material.TNT;
    public static final int slot = 21;

    public Demolitionist(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, getDuration() * 20, 0));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                    + player.getName() + ChatColor.GRAY + " uses the " + title + ChatColor.GRAY + " Kit Ability.");
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getDuration() * 20, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, getDuration() * 20, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, getDuration() * 20, 200));

        Location location = player.getLocation();

        if (location.getWorld() == null) return;

        for (int offsetX = -15; offsetX < 16; offsetX += 3) {
            for (int offsetZ = -15; offsetZ < 16; offsetZ += 3) {
                if (Math.abs(offsetX) < 6 && Math.abs(offsetZ) < 6)
                    continue;

                Location tntLocation = new Location(location.getWorld(),
                        location.getX() + offsetX, location.getY() - 1, location.getZ() + offsetZ);
                tntLocation.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation, TNTPrimed.class);

                Location tntLocation2 = new Location(location.getWorld(),
                        location.getX() + offsetX, location.getY() - 4, location.getZ() + offsetZ);
                tntLocation2.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation2, TNTPrimed.class);

                Location tntLocation3 = new Location(location.getWorld(),
                        location.getX() + offsetX, location.getY() - 7, location.getZ() + offsetZ);
                tntLocation3.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation3, TNTPrimed.class);

                Location tntLocation4 = new Location(location.getWorld(),
                        location.getX() + offsetX, location.getY() - 10, location.getZ() + offsetZ);
                tntLocation4.getBlock().setType(Material.AIR);
                location.getWorld().spawn(tntLocation4, TNTPrimed.class);
            }
        }

        this.runKitAbility(20, getDuration(), null, null);
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