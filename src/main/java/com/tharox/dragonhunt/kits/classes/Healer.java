package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.chat.Prefixes;
import com.tharox.dragonhunt.game.GameState;
import com.tharox.dragonhunt.shop.Upgrades;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

@DragonhuntKit
public class Healer extends Kit {
    static int heartsToAdd = 6;
    static int cooldown = 50;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Healer";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Instantly heal yourself " + ChatColor.WHITE + heartsToAdd + " ❤"
            + ChatColor.GRAY + ".\n"
            + HexColor.YELLOW + "» Heal can be increased\n"
            + HexColor.RED + "» Glow and Alert on Activation\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n"
            + HexColor.YELLOW + "» Cooldown can be reduced\n\n";
    public static final Material material = Material.POTION;
    public static final int slot = 20;

    public Healer(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        // Healer Kit Effectiveness
        int bonusHearts = 0;

        if (Upgrades.Effectiveness.getOrDefault(player, 0) != 0
                && GameState.selectedKit.getOrDefault(player, "").contains("Healer")) {
            bonusHearts = Upgrades.Effectiveness.get(player);
        }

        double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

        player.sendMessage(title + ChatColor.GRAY + " Kit Ability used.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));

        if ((player.getHealth() + (heartsToAdd + bonusHearts) * 2) > maxHealth) {
            player.setHealth(maxHealth);
        } else {
            player.setHealth(player.getHealth() + (heartsToAdd + bonusHearts) * 2);
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(Prefixes.getImportantPrefix() + Prefixes.getKitAbilityPrefix() + ChatColor.WHITE
                    + player.getName() + ChatColor.GRAY + " used the " + title + ChatColor.GRAY + " Kit Ability.");
        }
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
