package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Warrior extends Kit {
    static int duration = 12;
    static int cooldown = 60;

    public static final String title = HexColor.ASSASSIN + KitClass.ASSASSIN + " Warrior";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Deal " + ChatColor.WHITE + "2x" + ChatColor.GRAY + " Axe and Sword Damage\n"
            + ChatColor.GRAY + "for a short time.\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + duration + ChatColor.GRAY + " seconds\n"
            + HexColor.YELLOW + "⇡ Duration can be increased\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.DIAMOND_SWORD;
    public static final int slot = 2;

    public Warrior(Player player) {
        super(player);

        KitClass.assassins.add(player);
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
