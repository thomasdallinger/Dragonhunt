package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.chat.HexColor;
import com.tharox.dragonhunt.players.DamageAdjustmentListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Objects;

@DragonhuntKit
public class Healer extends Kit {
    static int heartsToAdd = 5;
    static int cooldown = 70;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Healer";
    public static final String lore = ""
            + ChatColor.DARK_GRAY + "ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "Instantly heal yourself " + ChatColor.WHITE + heartsToAdd + " ❤\n\n"
            + ChatColor.DARK_GRAY + "ᴅᴜʀᴀᴛɪᴏɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + "Instantly\n\n"
            + ChatColor.DARK_GRAY + "ᴄᴏᴏʟᴅᴏᴡɴ\n"
            + ChatColor.GRAY + ChatColor.WHITE + cooldown + ChatColor.GRAY + " seconds\n\n";
    public static final Material material = Material.POTION;
    public static final int slot = 20;

    public Healer(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

        player.sendMessage(title + ChatColor.GRAY + " Kit Ability used.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        if ((player.getHealth() + heartsToAdd * 2) > maxHealth) {
            player.setHealth(maxHealth);
        } else {
            player.setHealth(player.getHealth() + heartsToAdd * 2);
        }
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
