package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.upgrades.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@DragonhuntKit
public class Vex extends Kit {
    static int duration = 10;
    static int cooldown = 120;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Vex";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Applies " + ChatColor.WHITE + "1" + ChatColor.GRAY + " random negative effect to\n"
            + ChatColor.GRAY + "all other players for the abilities duration.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.WITHER_ROSE;
    public static final int slot = 24;

    public Vex(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    public void executeAbility() {
        Random random = new Random();
        List<PotionEffectType> negativeEffects = Arrays.asList(
                PotionEffectType.SLOW_DIGGING,
                PotionEffectType.WEAKNESS,
                PotionEffectType.POISON,
                PotionEffectType.LEVITATION,
                PotionEffectType.BLINDNESS);
        int amplifier = 0 + Upgrades.Effectiveness.getOrDefault(player, 0);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online != player) {

                online.addPotionEffect(new PotionEffect(negativeEffects.get(random.nextInt(negativeEffects.size())),
                        getDuration() * 20, amplifier));

                online.playSound(online.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 1);
                online.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                        + player.getName() + ChatColor.RED + " cursed you with a " + HexColor.RED + "negative effect"
                        + ChatColor.RED + " level " + HexColor.RED + (amplifier + 1)
                        + ChatColor.RED + " as a " + title + ChatColor.RED + ".");
            }
        }

        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
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
