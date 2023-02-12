package com.tharox.dragonhunt.kits.classes;

import com.tharox.dragonhunt.colors.HexColor;
import com.tharox.dragonhunt.spectate.Spectate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DragonhuntKit
public class Scout extends Kit {
    static int duration = Spectate.duration[0];
    static int cooldown = 120;

    public static final String title = HexColor.SUPPORT + KitClass.SUPPORT + " Vanisher";
    public static final String lore = ChatColor.GRAY + "Be in Spectator Mode for a short time!\n"
            + ChatColor.DARK_GRAY + "ᴋɪᴛ ᴀʙɪʟɪᴛʏ\n"
            + ChatColor.GRAY + "On activation you will\n"
            + ChatColor.GRAY + "be put in " + HexColor.SUPPORT + "Spectator Mode" + ChatColor.GRAY + ".\n"
            + ChatColor.GRAY + "Duration: " + HexColor.SUPPORT + duration + " sec\n"
            + ChatColor.GRAY + "Cooldown: " + HexColor.RED + cooldown + " sec\n";
    public static final Material material = Material.ENDER_PEARL;
    public static final int slot = 2;

    public Scout(Player player) {
        super(player);

        KitClass.supports.add(player);
    }

    @Override
    public void executeAbility() {
        new Spectate(player);
        player.sendMessage(title + ChatColor.GRAY + " Kit Ability is active.");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
