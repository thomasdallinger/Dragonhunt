package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@DragonhuntKit
public class Healer extends Kit {
    static int cooldown = 100;
    static int heartsToAdd = 6;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Healer";
    public static final String lore = ChatColor.GRAY + "Instantly heal yourself in critical moments!\n\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you get " + HexColor.SUPPORT + heartsToAdd + " Hearts"
            + ChatColor.GRAY + ".\n\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.POTION;
    public static final int slot = 20;

    public Healer(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability used.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        player.setHealth(player.getHealth() + heartsToAdd * 2);
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
