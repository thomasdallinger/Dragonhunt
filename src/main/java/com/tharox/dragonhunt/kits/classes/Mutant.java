package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.players.DamageAdjustmentListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Mutant extends Kit {
    static int duration = 8;
    static int cooldown = 50;

    public static final String title = HexColor.TANK + KitClass.TANK + " Mutant";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you receive: \n"
            + ChatColor.WHITE + "+40%" + ChatColor.GRAY + " Attack Speed\n"
            + ChatColor.WHITE + "+80%" + ChatColor.GRAY + " Mining Speed\n"
            + ChatColor.WHITE + "+" + DamageAdjustmentListener.mutantDamageBuff + " ❤"
            + ChatColor.GRAY + " Attack Damage\n"
            + ChatColor.WHITE + "+4 Absorption ❤\n"
            + HexColor.RED + "» Glow and Alert on Activation\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.DIAMOND_CHESTPLATE;
    public static final int slot = 12;

    public Mutant(Player player) {
        super(player);

        KitClass.tanks.add(player);
    }

    @Override
    public void executeAbility() {
        player.sendMessage(title + HexColor.GREEN + " Kit Ability activated.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GRAY
                    + " uses the " + title + ChatColor.GRAY + " Kit Ability.");
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration * 20, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration * 20, 1));

        this.runKitAbility(20, duration, null, null);
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
