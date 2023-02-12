package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.colors.HexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Unstoppable extends Kit {
    static int duration = 10;
    static int cooldown = 120;

    public static final String title = HexColor.TANK + KitClass.TANK + " Unstoppable";
    public static final String lore = ChatColor.GRAY + "Become unstoppable!\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you become " + HexColor.TANK + "invulnerable\n"
            + ChatColor.GRAY + "to all incoming damage.\n"
            + ChatColor.GRAY + "Duration: " + HexColor.TANK + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.ENDER_PEARL;
    public static final int slot = 2;

    public Unstoppable(Player player) {
        super(player);

        KitClass.tanks.add(player);
    }

    @Override
    public void executeAbility() {
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, 9));
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
