package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Sonic extends Kit {
    static int duration = 8;
    static int cooldown = 50;

    public static final String title = HexColor.FLANKER + KitClass.FLANKER + " Sonic";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Receive " + ChatColor.WHITE + "Speed 10" + ChatColor.GRAY + " for a short time\n"
            + ChatColor.GRAY + "alongside an initial speed burst for " + ChatColor.WHITE + "2 seconds"
            + ChatColor.GRAY + ".\n"
            + HexColor.YELLOW + "⇡ Burst duration can be increased\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.GOLDEN_BOOTS;
    public static final int slot = 31;

    public Sonic(Player player) {
        super(player);

        KitClass.flankers.add(player);
    }

    public void executeAbility() {
        int speedAmplifier = 10;

        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, getDuration() * 20, 0));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                    + player.getName() + ChatColor.GRAY + " uses the " + title + ChatColor.GRAY + " Kit Ability.");
        }

        Location playerLocation = player.getLocation();

        // Apply a flashy visual effect
        player.spawnParticle(Particle.FIREWORKS_SPARK, playerLocation, 100, 1, 1, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getDuration() * 20,
                speedAmplifier - 1));

        // Create a dash ability with increased speed for a short burst
        speedBurstAbility();

        this.runKitAbility(5, getDuration(), null, this::spawnSpeedTrail);
    }

    // Create a trail of particles behind Sonic to represent speed
    private void spawnSpeedTrail() {
        player.getWorld().spawnParticle(Particle.SPELL_INSTANT, player.getLocation(),
                40, 0.2, 0.2, 0.2, 0.1);
    }

    private void speedBurstAbility() {
        int dashSpeedAmplifier = 30;
        int dashDuration = 2 + Upgrades.Effectiveness.getOrDefault(player, 0);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dashDuration * 20,
                dashSpeedAmplifier - 1));
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

